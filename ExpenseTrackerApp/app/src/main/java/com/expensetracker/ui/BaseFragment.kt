package com.expensetracker.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.expensetracker.MainActivity
import com.expensetracker.viewmodel.TransactionViewModel
import com.expensetracker.viewmodel.WalletViewModel

/**
 * All fragments extend this instead of Fragment directly.
 * Provides transactionViewModel and walletViewModel already wired
 * to the correct factory — no boilerplate in each fragment.
 */
open class BaseFragment : Fragment() {

    private val factory by lazy {
        (requireActivity() as MainActivity).factory
    }

    val transactionViewModel: TransactionViewModel by activityViewModels { factory }
    val walletViewModel: WalletViewModel by activityViewModels { factory }
}