package com.expensetracker.data.db.dao

import androidx.room.*
import com.expensetracker.data.db.entities.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet): Long

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    /** All wallets for this user — used for the card list on the Wallet screen */
    @Query("SELECT * FROM wallet WHERE userId = :userId ORDER BY createdAt ASC")
    fun getWalletsForUser(userId: Long): Flow<List<Wallet>>

    /** Single wallet — used for the detail screen */
    @Query("SELECT * FROM wallet WHERE id = :walletId LIMIT 1")
    fun getWalletById(walletId: Long): Flow<Wallet?>

    /** Add funds: increases balance and updates initialBalance stamp if needed */
    @Query("""
        UPDATE wallet 
        SET balance = balance + :amount, updatedAt = :updatedAt 
        WHERE id = :walletId
    """)
    suspend fun addFunds(walletId: Long, amount: Double, updatedAt: Long = System.currentTimeMillis())

    /** Deduct funds when an expense is recorded against this wallet */
    @Query("""
        UPDATE wallet 
        SET balance = balance - :amount, updatedAt = :updatedAt 
        WHERE id = :walletId
    """)
    suspend fun deductFunds(walletId: Long, amount: Double, updatedAt: Long = System.currentTimeMillis())

    /** Reverse a deduction (e.g. when deleting an expense transaction) */
    @Query("""
        UPDATE wallet 
        SET balance = balance + :amount, updatedAt = :updatedAt 
        WHERE id = :walletId
    """)
    suspend fun refundFunds(walletId: Long, amount: Double, updatedAt: Long = System.currentTimeMillis())
}