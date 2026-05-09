package com.expensetracker.data.repository

import com.expensetracker.data.db.dao.BudgetDao
import com.expensetracker.data.db.dao.TransactionDao
import com.expensetracker.data.db.entities.Budget
import com.expensetracker.data.db.entities.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions(userId: Long) = transactionDao.getAllTransactions(userId)

    fun getTransactionsByMonth(userId: Long, yearMonth: String) =
        transactionDao.getTransactionsByMonth(userId, yearMonth)

//    fun getTransactionsByDay(userId: Long, startOfDay: Long, endOfDay: Long) =
//        transactionDao.getTransactionsByDay(userId, startOfDay, endOfDay)

    fun getTotalExpenseForMonth(userId: Long, yearMonth: String) =
        transactionDao.getTotalExpenseForMonth(userId, yearMonth)

    fun getCategoryBreakdown(userId: Long, yearMonth: String) =
        transactionDao.getCategoryBreakdown(userId, yearMonth)

    fun getDailyTotalsForMonth(userId: Long, yearMonth: String) =
        transactionDao.getDailyTotalsForMonth(userId, yearMonth)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)
    // ── Per-wallet ────────────────────────────────────────────────────────────

    fun getTransactionsByWallet(walletId: Long) =
        transactionDao.getTransactionsByWallet(walletId)

    fun getTotalIncomeForWallet(walletId: Long) =
        transactionDao.getTotalIncomeForWallet(walletId)

    fun getTotalExpenseForWallet(walletId: Long) =
        transactionDao.getTotalExpenseForWallet(walletId)

    fun getTransactionCountForWallet(walletId: Long) =
        transactionDao.getTransactionCountForWallet(walletId)
}