//package com.expensetracker.viewmodel
//
//import android.app.Application
//import androidx.lifecycle.*
//import com.expensetracker.ExpenseTrackerApp
//import com.expensetracker.utils.SessionManager
//import kotlinx.coroutines.launch
//
//class WalletViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val app = application as ExpenseTrackerApp
//    private val walletRepo = app.walletRepository
//    private val session = SessionManager(application)
//    private val userId = session.getUserId()
//
//    val wallet = walletRepo.getWallet(userId).asLiveData()
//
//    fun addFunds(amount: Double) {
//        viewModelScope.launch {
//            walletRepo.addFunds(userId, amount)
//        }
//    }
//}

package com.expensetracker.viewmodel

import androidx.lifecycle.*
import com.expensetracker.data.db.entities.Wallet
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.data.repository.WalletRepository
import kotlinx.coroutines.launch

class WalletViewModel(
    private val repository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val userId: Long
) : ViewModel() {

    // ── All wallets for this user ─────────────────────────────────────────────

    val wallets: LiveData<List<Wallet>> = repository
        .getWalletsForUser(userId)
        .asLiveData()

    // ── Currently selected wallet (for detail screen) ─────────────────────────

    private val _selectedWalletId = MutableLiveData<Long>()

    fun selectWallet(walletId: Long) {
        _selectedWalletId.value = walletId
    }

    val selectedWallet: LiveData<Wallet?> = _selectedWalletId.switchMap { id ->
        repository.getWalletById(id).asLiveData()
    }

    // These use TransactionRepository since wallet-scoped queries live in TransactionDao
    val walletTransactions = _selectedWalletId.switchMap { id ->
        transactionRepository.getTransactionsByWallet(id).asLiveData()
    }

    val walletTotalIncome = _selectedWalletId.switchMap { id ->
        transactionRepository.getTotalIncomeForWallet(id).asLiveData()
    }

    val walletTotalExpense = _selectedWalletId.switchMap { id ->
        transactionRepository.getTotalExpenseForWallet(id).asLiveData()
    }

    val walletTransactionCount = _selectedWalletId.switchMap { id ->
        transactionRepository.getTransactionCountForWallet(id).asLiveData()
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    fun createWallet(name: String, initialBalance: Double) {
        viewModelScope.launch {
            val wallet = Wallet(
                userId         = userId,
                name           = name,
                initialBalance = initialBalance,
                balance        = initialBalance
            )
            repository.createWallet(wallet)
        }
    }

    fun addFunds(walletId: Long, amount: Double) {
        viewModelScope.launch {
            repository.addFunds(walletId, amount)
        }
    }
}