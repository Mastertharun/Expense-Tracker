package com.expensetracker.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.expensetracker.ui.base.BaseFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.databinding.FragmentWalletDetailBinding
import com.expensetracker.ui.home.TransactionAdapter
import com.expensetracker.ui.home.TransactionListItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

class WalletDetailFragment : BaseFragment() {

    private var _binding: FragmentWalletDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var txAdapter: TransactionAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private var showAll = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        txAdapter = TransactionAdapter { transactionViewModel.deleteTransaction(it) }
        binding.rvWalletTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = txAdapter
        }

        // ── Wallet header info ────────────────────────────────────────────────
        walletViewModel.selectedWallet.observe(viewLifecycleOwner) { wallet ->
            wallet ?: return@observe
            binding.tvDetailWalletName.text = wallet.name
            binding.tvDetailBalance.text = currencyFormat.format(wallet.balance)
            binding.tvInitialBalance.text =
                "Initially funded: ${currencyFormat.format(wallet.initialBalance)}"
        }

        // ── Stats ─────────────────────────────────────────────────────────────
        walletViewModel.walletTotalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvDetailIncome.text = currencyFormat.format(income ?: 0.0)
        }

        walletViewModel.walletTotalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvDetailExpense.text = currencyFormat.format(expense ?: 0.0)
        }

        walletViewModel.walletTransactionCount.observe(viewLifecycleOwner) { count ->
            binding.tvDetailTxCount.text = (count ?: 0).toString()
        }

        // ── Transaction list ──────────────────────────────────────────────────
        walletViewModel.walletTransactions.observe(viewLifecycleOwner) { list ->
            renderList(list)
        }

        binding.tvDetailViewAll.setOnClickListener {
            showAll = !showAll
            binding.tvDetailViewAll.text = if (showAll) "Show less" else "View all"
            walletViewModel.walletTransactions.value?.let { renderList(it) }
        }

        // ── Add funds ─────────────────────────────────────────────────────────
        binding.btnAddFunds.setOnClickListener { showAddFundsDialog() }
    }

    private fun renderList(list: List<com.expensetracker.data.db.entities.Transaction>) {
        val displayed = if (showAll) list else list.take(5)
        txAdapter.submitList(displayed.map { TransactionListItem.Item(it) })
        binding.tvDetailViewAll.visibility = if (list.size > 5) View.VISIBLE else View.GONE
    }

    private fun showAddFundsDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.expensetracker.R.layout.dialog_add_funds, null)
        val etAmount = dialogView.findViewById<TextInputEditText>(com.expensetracker.R.id.etFundsAmount)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Funds")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amount = etAmount.text.toString().toDoubleOrNull()
                val walletId = walletViewModel.selectedWallet.value?.id ?: return@setPositiveButton
                if (amount != null && amount > 0) {
                    walletViewModel.addFunds(walletId, amount)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}