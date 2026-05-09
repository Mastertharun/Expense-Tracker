package com.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.ExpenseTrackerApp

/**
 * Single factory registered on the Activity.
 * Gets all repositories from ExpenseTrackerApp so there's only ever
 * one instance of each repo across the whole app.
 */
class AppViewModelFactory(
    private val application: Application,
    private val userId: Long
) : ViewModelProvider.Factory {

    private val app get() = application as ExpenseTrackerApp

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(TransactionViewModel::class.java) ->
            TransactionViewModel(
                transactionRepo = app.transactionRepository,
                walletRepo      = app.walletRepository,
                budgetRepo      = app.budgetRepository,
                userId          = userId
            ) as T

        modelClass.isAssignableFrom(WalletViewModel::class.java) ->
            WalletViewModel(
                repository            = app.walletRepository,
                transactionRepository = app.transactionRepository,
                userId                = userId
            ) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}