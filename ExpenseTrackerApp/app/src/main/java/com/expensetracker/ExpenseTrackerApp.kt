//package com.expensetracker
//
//import android.app.Application
//import com.expensetracker.data.db.AppDatabase
//import com.expensetracker.data.repository.*
//
//class ExpenseTrackerApp : Application() {
//
//    val database by lazy { AppDatabase.getInstance(this) }
//
//    val userRepository by lazy { UserRepository(database.userDao()) }
//    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
//    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }
//    val walletRepository by lazy { WalletRepository(database.walletDao()) }
//}
package com.expensetracker

import android.app.Application
import com.expensetracker.data.db.AppDatabase
import com.expensetracker.data.repository.BudgetRepository
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.data.repository.UserRepository
import com.expensetracker.data.repository.WalletRepository

class ExpenseTrackerApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }

    // Each repository takes only the DAO(s) it actually needs
    val userRepository        by lazy { UserRepository(database.userDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val budgetRepository      by lazy { BudgetRepository(database.budgetDao()) }
    val walletRepository      by lazy { WalletRepository(database.walletDao()) }
}