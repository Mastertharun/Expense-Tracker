////package com.expensetracker.viewmodel
////
////import android.app.Application
////import androidx.lifecycle.*
////import com.expensetracker.ExpenseTrackerApp
////import com.expensetracker.data.db.entities.Transaction
////import com.expensetracker.utils.DateUtils
////import com.expensetracker.utils.SessionManager
////import kotlinx.coroutines.flow.*
////import kotlinx.coroutines.launch
////
////class TransactionViewModel(application: Application) : AndroidViewModel(application) {
////
////    private val app = application as ExpenseTrackerApp
////    private val transactionRepo = app.transactionRepository
////    private val budgetRepo = app.budgetRepository
////    private val walletRepo = app.walletRepository
////    private val session = SessionManager(application)
////    val userId = session.getUserId()
////
////    private val _selectedYearMonth = MutableStateFlow(DateUtils.getCurrentMonthYear())
////    val selectedYearMonth: StateFlow<String> = _selectedYearMonth
////
////    val transactions = userId.let { id ->
////        if (id == -1L) flowOf(emptyList())
////        else transactionRepo.getAllTransactions(id)
////    }.asLiveData()
////
////    val monthlyTransactions = _selectedYearMonth.flatMapLatest { ym ->
////        if (userId == -1L) flowOf(emptyList())
////        else transactionRepo.getTransactionsByMonth(userId, ym)
////    }.asLiveData()
////
////    val totalMonthlyExpense = _selectedYearMonth.flatMapLatest { ym ->
////        if (userId == -1L) flowOf(0.0)
////        else transactionRepo.getTotalExpenseForMonth(userId, ym).map { it ?: 0.0 }
////    }.asLiveData()
////
////    val monthlyBudget = _selectedYearMonth.flatMapLatest { ym ->
////        val parts = ym.split("-")
////        val year = parts[0].toInt()
////        val month = parts[1].toInt()
////        if (userId == -1L) flowOf(null)
////        else budgetRepo.getBudgetForMonth(userId, month, year)
////    }.asLiveData()
////
////    val categoryBreakdown = _selectedYearMonth.flatMapLatest { ym ->
////        if (userId == -1L) flowOf(emptyList())
////        else transactionRepo.getCategoryBreakdown(userId, ym)
////    }.asLiveData()
////
////    val dailyTotals = _selectedYearMonth.flatMapLatest { ym ->
////        if (userId == -1L) flowOf(emptyList())
////        else transactionRepo.getDailyTotalsForMonth(userId, ym)
////    }.asLiveData()
////
////    fun setYearMonth(year: Int, month: Int) {
////        _selectedYearMonth.value = DateUtils.getYearMonth(year, month)
////    }
////
////    fun addTransaction(amount: Double, category: String, note: String, date: Long, type: String = "EXPENSE") {
////        viewModelScope.launch {
////            val transaction = Transaction(
////                userId = userId,
////                amount = amount,
////                category = category,
////                note = note,
////                date = date,
////                type = type
////            )
////            transactionRepo.insertTransaction(transaction)
////            if (type == "EXPENSE") {
////                walletRepo.deductFunds(userId, amount)
////            }
////        }
////    }
////
////    fun deleteTransaction(transaction: Transaction) {
////        viewModelScope.launch {
////            transactionRepo.deleteTransaction(transaction)
////            if (transaction.type == "EXPENSE") {
////                walletRepo.addFunds(userId, transaction.amount)
////            }
////        }
////    }
////
////    fun saveBudget(amount: Double) {
////        val parts = _selectedYearMonth.value.split("-")
////        val year = parts[0].toInt()
////        val month = parts[1].toInt()
////        viewModelScope.launch {
////            budgetRepo.saveBudget(userId, month, year, amount)
////        }
////    }
////}
//package com.expensetracker.viewmodel
//
//import androidx.lifecycle.*
//import com.expensetracker.data.db.entities.Transaction
//import com.expensetracker.data.db.entities.TransactionType
//import com.expensetracker.data.repository.TransactionRepository
//import com.expensetracker.data.repository.WalletRepository
//import com.expensetracker.data.repository.BudgetRepository
//import com.expensetracker.utils.DateUtils
//import kotlinx.coroutines.launch
//
//class TransactionViewModel(
//    private val transactionRepo: TransactionRepository,
//    private val walletRepo: WalletRepository,
//    private val budgetRepo: BudgetRepository,
//    private val userId: Long
//) : ViewModel() {
//
//    private val currentYearMonth = DateUtils.getCurrentMonthYear() // e.g. "2025-06"
//
//    val monthlyTransactions: LiveData<List<Transaction>> =
//        transactionRepo.getTransactionsByMonth(userId, currentYearMonth).asLiveData()
//
//    val totalMonthlyExpense: LiveData<Double> =
//        transactionRepo.getTotalExpenseForMonth(userId, currentYearMonth)
//            .asLiveData()
//            .map { it ?: 0.0 }
//
//    val monthlyBudget = budgetRepo.getCurrentMonthBudget(userId).asLiveData()
//
//    fun addTransaction(
//        amount: Double,
//        category: String,
//        note: String,
//        date: Long,
//        type: String,
//        walletId: Long          // ← NEW: caller passes the selected wallet id
//    ) {
//        viewModelScope.launch {
//            val tx = Transaction(
//                userId = userId,
//                walletId = walletId,
//                amount = amount,
//                category = category,
//                note = note,
//                date = date,
//                type = type
//            )
//            transactionRepo.insertTransaction(tx)
//
//            // Keep wallet balance in sync
//            if (type == TransactionType.EXPENSE.name) {
//                walletRepo.deductFunds(walletId, amount)
//            } else {
//                walletRepo.addFunds(walletId, amount)
//            }
//        }
//    }
//
//    fun deleteTransaction(transaction: Transaction) {
//        viewModelScope.launch {
//            transactionRepo.deleteTransaction(transaction)
//            // Reverse the balance effect
//            if (transaction.type == TransactionType.EXPENSE.name) {
//                walletRepo.refundFunds(transaction.walletId, transaction.amount)
//            } else {
//                walletRepo.deductFunds(transaction.walletId, transaction.amount)
//            }
//        }
//    }
//
//    fun saveBudget(amount: Double) {
//        viewModelScope.launch { budgetRepo.saveBudget(userId, amount) }
//    }
//}
package com.expensetracker.viewmodel

import androidx.lifecycle.*
import com.expensetracker.data.db.entities.Transaction
import com.expensetracker.data.db.entities.TransactionType
import com.expensetracker.data.repository.BudgetRepository
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.data.repository.WalletRepository
import com.expensetracker.utils.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(
    private val transactionRepo: TransactionRepository,
    private val walletRepo: WalletRepository,
    private val budgetRepo: BudgetRepository,
    private val userId: Long
) : ViewModel() {

    // ── Selected month (drives Statistics + Calendar + Home) ─────────────────

    private val _selectedYearMonth = MutableStateFlow(DateUtils.getCurrentMonthYear())
    val selectedYearMonth: StateFlow<String> = _selectedYearMonth

    fun setYearMonth(year: Int, month: Int) {
        _selectedYearMonth.value = DateUtils.getYearMonth(year, month)
    }

    // ── Home: always current month ────────────────────────────────────────────

    val monthlyTransactions: LiveData<List<Transaction>> =
        _selectedYearMonth.flatMapLatest { ym ->
            transactionRepo.getTransactionsByMonth(userId, ym)
        }.asLiveData()

    val totalMonthlyExpense: LiveData<Double> =
        _selectedYearMonth.flatMapLatest { ym ->
            transactionRepo.getTotalExpenseForMonth(userId, ym)
                .map { it ?: 0.0 }
        }.asLiveData()

    // ── Budget: follows selected month ────────────────────────────────────────

    val monthlyBudget: LiveData<com.expensetracker.data.db.entities.Budget?> =
        _selectedYearMonth.flatMapLatest { ym ->
            val parts = ym.split("-")
            val year  = parts[0].toInt()
            val month = parts[1].toInt()
            budgetRepo.getBudgetForMonth(userId, month, year)
        }.asLiveData()

    // ── Statistics + Calendar ─────────────────────────────────────────────────

    val categoryBreakdown = _selectedYearMonth.flatMapLatest { ym ->
        transactionRepo.getCategoryBreakdown(userId, ym)
    }.asLiveData()

    val dailyTotals = _selectedYearMonth.flatMapLatest { ym ->
        transactionRepo.getDailyTotalsForMonth(userId, ym)
    }.asLiveData()

    val allTransactions: LiveData<List<Transaction>> =
        transactionRepo.getAllTransactions(userId).asLiveData()

    // ── Mutations ─────────────────────────────────────────────────────────────

    fun addTransaction(
        amount: Double,
        category: String,
        note: String,
        date: Long,
        type: String,
        walletId: Long
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                userId   = userId,
                walletId = walletId,
                amount   = amount,
                category = category,
                note     = note,
                date     = date,
                type     = type
            )
            transactionRepo.insertTransaction(tx)
            if (type == TransactionType.EXPENSE.name) {
                walletRepo.deductFunds(walletId, amount)
            } else {
                walletRepo.addFunds(walletId, amount)
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepo.deleteTransaction(transaction)
            if (transaction.type == TransactionType.EXPENSE.name) {
                walletRepo.refundFunds(transaction.walletId, transaction.amount)
            } else {
                walletRepo.deductFunds(transaction.walletId, transaction.amount)
            }
        }
    }

    fun saveBudget(amount: Double) {
        val parts = _selectedYearMonth.value.split("-")
        val year  = parts[0].toInt()
        val month = parts[1].toInt()
        viewModelScope.launch {
            budgetRepo.saveBudget(userId, month, year, amount)
        }
    }
}