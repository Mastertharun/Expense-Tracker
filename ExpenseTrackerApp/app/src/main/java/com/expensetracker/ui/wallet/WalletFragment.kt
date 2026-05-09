package com.expensetracker.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.expensetracker.ui.base.BaseFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.databinding.FragmentWalletBinding
import com.expensetracker.ui.home.TransactionAdapter
import com.expensetracker.ui.home.TransactionListItem
import com.expensetracker.utils.SessionManager
import com.expensetracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

class WalletFragment : BaseFragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!


    private lateinit var walletCardAdapter: WalletCardAdapter
    private lateinit var txAdapter: TransactionAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // Tracks whether the recent-list is expanded
    private var showAll = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        binding.tvWalletOwner.text = session.getUserName()

        // ── Wallet card list ─────────────────────────────────────────────────
        walletCardAdapter = WalletCardAdapter(
            onWalletClick = { wallet ->
                walletViewModel.selectWallet(wallet.id)
                findNavController().navigate(R.id.action_wallet_to_detail)
            },
            onAddClick = { showCreateWalletDialog() }
        )
        binding.rvWallets.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = walletCardAdapter
        }

        // ── Recent transactions list ─────────────────────────────────────────
        txAdapter = TransactionAdapter { transactionViewModel.deleteTransaction(it) }
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = txAdapter
        }

        // ── Observe wallet list ──────────────────────────────────────────────
        walletViewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletCardAdapter.submitList(wallets)
        }

        // ── Observe recent transactions (all wallets, this month) ────────────
        transactionViewModel.monthlyTransactions.observe(viewLifecycleOwner) { list ->
            renderTransactionList(list)
        }

        // ── View all toggle ──────────────────────────────────────────────────
        binding.tvViewAll.setOnClickListener {
            showAll = !showAll
            binding.tvViewAll.text = if (showAll) "Show less" else "View all"
            transactionViewModel.monthlyTransactions.value?.let { renderTransactionList(it) }
        }
    }

    private fun renderTransactionList(list: List<com.expensetracker.data.db.entities.Transaction>) {
        val displayed = if (showAll) list else list.take(5)
        val items = displayed.map { TransactionListItem.Item(it) }
        txAdapter.submitList(items)
        binding.tvViewAll.visibility = if (list.size > 5) View.VISIBLE else View.GONE
    }

    private fun showCreateWalletDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.expensetracker.R.layout.dialog_create_wallet, null)
        val etName    = dialogView.findViewById<TextInputEditText>(com.expensetracker.R.id.etWalletName)
        val etBalance = dialogView.findViewById<TextInputEditText>(com.expensetracker.R.id.etInitialBalance)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create New Wallet")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = etName.text.toString().trim()
                val balance = etBalance.text.toString().toDoubleOrNull() ?: 0.0
                if (name.isNotBlank()) {
                    walletViewModel.createWallet(name, balance)
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