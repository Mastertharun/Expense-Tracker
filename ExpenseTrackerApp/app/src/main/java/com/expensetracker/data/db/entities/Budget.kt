package com.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val month: Int,   // 1-12
    val year: Int,
    val amount: Double,
    val updatedAt: Long = System.currentTimeMillis()
)
