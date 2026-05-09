package com.expensetracker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.expensetracker.databinding.ActivityMainBinding
import com.expensetracker.ui.home.AddExpenseBottomSheet
import com.expensetracker.utils.SessionManager
import com.expensetracker.viewmodel.AppViewModelFactory
import com.expensetracker.viewmodel.TransactionViewModel
import com.expensetracker.viewmodel.WalletViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Both ViewModels are created here with the shared factory.
    // Every fragment that calls activityViewModels() will receive
    // the same instances automatically.
    val factory by lazy {
        val userId = SessionManager(this).getUserId()
        AppViewModelFactory(application, userId)
    }

    val transactionViewModel: TransactionViewModel by viewModels { factory }
    val walletViewModel: WalletViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Bottom nav wiring (assumes you have a bottom_nav_menu.xml)
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.fabAddExpense.setOnClickListener {
            AddExpenseBottomSheet().show(supportFragmentManager, "AddExpense")
        }
    }
}