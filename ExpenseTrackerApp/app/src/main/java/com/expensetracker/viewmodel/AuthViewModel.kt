package com.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.expensetracker.ExpenseTrackerApp
import com.expensetracker.data.db.entities.User
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = (application as ExpenseTrackerApp).userRepository
    private val walletRepository = (application as ExpenseTrackerApp).walletRepository

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email address")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.register(name, email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                walletRepository.initWallet(user.id)
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password are required")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val user = userRepository.login(email, password)
            if (user != null) {
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
