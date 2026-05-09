package com.expensetracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.expensetracker.ui.base.BaseFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.R
import com.expensetracker.databinding.FragmentHomeBinding
import com.expensetracker.utils.DateUtils
import com.expensetracker.utils.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TransactionAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        binding.tvUserName.text = "Hi, ${session.getUserName()} 👋"

        // ── RecyclerView setup (THIS was the missing piece) ───────────────────
        adapter = TransactionAdapter { transaction ->
            transactionViewModel.deleteTransaction(transaction)
        }
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter

//        // ── FAB / Add button ──────────────────────────────────────────────────
//        binding.fabAddTransaction.setOnClickListener {
//            AddExpenseBottomSheet().show(childFragmentManager, "AddExpense")
//        }

        // ── Budget dialog ─────────────────────────────────────────────────────
        binding.btnSetBudget.setOnClickListener {
            SetBudgetDialog().show(childFragmentManager, "SetBudget")
        }

        // ── Transactions list ─────────────────────────────────────────────────
        transactionViewModel.monthlyTransactions.observe(viewLifecycleOwner) { transactions ->
            val displayList = groupTransactions(transactions)
            adapter.submitList(displayList)
            binding.emptyStateView.visibility =
                if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }

        // ── Spent amount ──────────────────────────────────────────────────────
        transactionViewModel.totalMonthlyExpense.observe(viewLifecycleOwner) { spent ->
            binding.tvTotalSpent.text = currencyFormat.format(spent)
            updateProgress(spent)
        }

        // ── Budget bar ────────────────────────────────────────────────────────
        transactionViewModel.monthlyBudget.observe(viewLifecycleOwner) { budget ->
            val amount = budget?.amount ?: 0.0
            binding.tvBudget.text = if (amount > 0)
                "Budget: ${currencyFormat.format(amount)}"
            else
                "No budget set"
            val spent = transactionViewModel.totalMonthlyExpense.value ?: 0.0
            updateProgressWithBudget(spent, amount)
        }

        binding.tvMonth.text = DateUtils.formatFullMonth(
            DateUtils.getCurrentYear(), DateUtils.getCurrentMonth()
        )
    }

    private fun updateProgress(spent: Double) {
        val budget = transactionViewModel.monthlyBudget.value?.amount ?: 0.0
        updateProgressWithBudget(spent, budget)
    }

    private fun updateProgressWithBudget(spent: Double, budget: Double) {
        if (budget > 0) {
            val progress = ((spent / budget) * 100).toInt().coerceIn(0, 100)
            binding.progressBudget.progress = progress
            binding.tvBudgetLeft.text =
                "Left: ${currencyFormat.format((budget - spent).coerceAtLeast(0.0))}"
            val color = when {
                progress >= 90 -> requireContext().getColor(R.color.expense_red)
                progress >= 70 -> requireContext().getColor(R.color.expense_orange)
                else           -> requireContext().getColor(R.color.accent_green)
            }
            binding.progressBudget.setIndicatorColor(color)
        } else {
            binding.progressBudget.progress = 0
            binding.tvBudgetLeft.text = "Set a budget to track"
        }
    }

    private fun groupTransactions(
        transactions: List<com.expensetracker.data.db.entities.Transaction>
    ): List<TransactionListItem> {
        val headerFormat = SimpleDateFormat("dd EEEE MMM yyyy", Locale.getDefault())
        return transactions
            .sortedByDescending { it.date }
            .groupBy { headerFormat.format(Date(it.date)) }
            .flatMap { (dateString, items) ->
                listOf(TransactionListItem.Header(dateString)) +
                        items.map { TransactionListItem.Item(it) }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}