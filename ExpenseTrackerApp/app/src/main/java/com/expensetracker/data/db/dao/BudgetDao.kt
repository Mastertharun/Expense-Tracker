package com.expensetracker.data.db.dao

import androidx.room.*
import com.expensetracker.data.db.entities.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month AND year = :year LIMIT 1")
    fun getBudgetForMonth(userId: Long, month: Int, year: Int): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY year DESC, month DESC")
    fun getAllBudgets(userId: Long): Flow<List<Budget>>

    @Query("DELETE FROM budgets WHERE userId = :userId AND month = :month AND year = :year")
    suspend fun deleteBudgetForMonth(userId: Long, month: Int, year: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudget(budget: Budget): Long

    @Query("SELECT * FROM budgets WHERE userId = :userId LIMIT 1")
    fun getBudget(userId: Long): Flow<Budget?>
}
