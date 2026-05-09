package com.expensetracker.data.db.dao

import androidx.room.*
import com.expensetracker.data.db.entities.Transaction
import kotlinx.coroutines.flow.Flow

data class CategorySum(val category: String, val total: Double)
data class DailyTotal(val day: String, val total: Double)

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // ── Global (all wallets for this user) ────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactions(userId: Long): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE userId = :userId 
        AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth
        ORDER BY date DESC
    """)
    fun getTransactionsByMonth(userId: Long, yearMonth: String): Flow<List<Transaction>>

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE userId = :userId 
        AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth
        AND type = 'EXPENSE'
    """)
    fun getTotalExpenseForMonth(userId: Long, yearMonth: String): Flow<Double?>

    @Query("""
        SELECT category, SUM(amount) as total 
        FROM transactions 
        WHERE userId = :userId 
        AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth
        AND type = 'EXPENSE'
        GROUP BY category 
        ORDER BY total DESC
    """)
    fun getCategoryBreakdown(userId: Long, yearMonth: String): Flow<List<CategorySum>>

    @Query("""
        SELECT strftime('%d', date/1000, 'unixepoch') as day, SUM(amount) as total
        FROM transactions 
        WHERE userId = :userId 
        AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth
        AND type = 'EXPENSE'
        GROUP BY day
    """)
    fun getDailyTotalsForMonth(userId: Long, yearMonth: String): Flow<List<DailyTotal>>

    // ── Per-wallet queries ────────────────────────────────────────────────────

    /** All transactions for one specific wallet */
    @Query("""
        SELECT * FROM transactions 
        WHERE walletId = :walletId 
        ORDER BY date DESC
    """)
    fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>>

    /** Monthly transactions for one specific wallet */
    @Query("""
        SELECT * FROM transactions 
        WHERE walletId = :walletId 
        AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth
        ORDER BY date DESC
    """)
    fun getMonthlyTransactionsByWallet(walletId: Long, yearMonth: String): Flow<List<Transaction>>

    /** Total income for a wallet (all time) */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE walletId = :walletId AND type = 'INCOME'
    """)
    fun getTotalIncomeForWallet(walletId: Long): Flow<Double>

    /** Total expense for a wallet (all time) */
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE walletId = :walletId AND type = 'EXPENSE'
    """)
    fun getTotalExpenseForWallet(walletId: Long): Flow<Double>

    /** Transaction count for a wallet */
    @Query("SELECT COUNT(*) FROM transactions WHERE walletId = :walletId")
    fun getTransactionCountForWallet(walletId: Long): Flow<Int>
}