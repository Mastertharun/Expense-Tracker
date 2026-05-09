package com.expensetracker.ui.home

import com.expensetracker.data.db.entities.Transaction

sealed class TransactionListItem {
    data class Header(val dateString: String) : TransactionListItem()
    data class Item(val transaction: Transaction) : TransactionListItem()
}
