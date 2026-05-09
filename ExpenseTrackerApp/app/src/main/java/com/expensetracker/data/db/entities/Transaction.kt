package com.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE }

enum class TransactionCategory(val displayName: String, val emoji: String) {
    FOOD("Food", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    ENTERTAINMENT("Entertainment", "🎬"),
    HEALTH("Health", "💊"),
    UTILITIES("Utilities", "💡"),
    EDUCATION("Education", "📚"),
    SALARY("Salary", "💰"),
    INVESTMENT("Investment", "📈"),
    OTHER("Other", "📦")
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val walletId: Long,         // which wallet this belongs to
    val amount: Double,
    val category: String,
    val note: String,
    val date: Long,             // Unix epoch millis
    val type: String            // TransactionType.name
)