package com.expensetracker.data.repository

import com.expensetracker.data.db.dao.TransactionDao
import com.expensetracker.data.db.dao.WalletDao
import com.expensetracker.data.db.entities.Wallet
import kotlinx.coroutines.flow.Flow

class WalletRepository(
    private val walletDao: WalletDao
) {
    fun getWalletsForUser(userId: Long): Flow<List<Wallet>> =
        walletDao.getWalletsForUser(userId)

    fun getWalletById(walletId: Long): Flow<Wallet?> =
        walletDao.getWalletById(walletId)

    suspend fun createWallet(wallet: Wallet): Long =
        walletDao.insertWallet(wallet)

    suspend fun initWallet(userId: Long) {
        walletDao.insertWallet(
            Wallet(
                userId         = userId,
                name           = "Cash",
                initialBalance = 0.0,
                balance        = 0.0
            )
        )
    }

    suspend fun addFunds(walletId: Long, amount: Double) =
        walletDao.addFunds(walletId, amount)

    suspend fun deductFunds(walletId: Long, amount: Double) =
        walletDao.deductFunds(walletId, amount)

    suspend fun refundFunds(walletId: Long, amount: Double) =
        walletDao.refundFunds(walletId, amount)
}