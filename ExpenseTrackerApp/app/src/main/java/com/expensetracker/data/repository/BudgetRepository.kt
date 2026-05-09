package com.expensetracker.data.repository

import com.expensetracker.data.db.dao.BudgetDao
import com.expensetracker.data.db.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class BudgetRepository(private val budgetDao: BudgetDao) {

    /** Get budget for a specific month (month is 1-based: 1=Jan … 12=Dec) */
    fun getBudgetForMonth(userId: Long, month: Int, year: Int): Flow<Budget?> =
        budgetDao.getBudgetForMonth(userId, month, year)

    /** Convenience — current calendar month */
    fun getCurrentMonthBudget(userId: Long): Flow<Budget?> {
        val cal = Calendar.getInstance()
        return budgetDao.getBudgetForMonth(
            userId,
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }

    fun getAllBudgets(userId: Long): Flow<List<Budget>> =
        budgetDao.getAllBudgets(userId)

    /** Save budget for an explicit month/year — called by TransactionViewModel.saveBudget() */
    suspend fun saveBudget(userId: Long, month: Int, year: Int, amount: Double) {
        budgetDao.saveBudget(
            Budget(
                userId    = userId,
                month     = month,
                year      = year,
                amount    = amount
            )
        )
    }

    /** Convenience overload — saves for the current calendar month */
    suspend fun saveBudget(userId: Long, amount: Double) {
        val cal = Calendar.getInstance()
        saveBudget(userId, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), amount)
    }

    suspend fun deleteBudgetForMonth(userId: Long, month: Int, year: Int) =
        budgetDao.deleteBudgetForMonth(userId, month, year)
}