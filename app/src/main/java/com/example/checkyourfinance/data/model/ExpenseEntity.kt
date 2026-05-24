package com.example.checkyourfinance.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["userId"])]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val amount: Double,
    val transactionType: String = "EXPENSE",
    val category: String,
    val categoryType: String,
    val paymentMethod: String = "OTHER",
    val date: String,
    val description: String,
    val isFavorite: Boolean,
    val createdAt: Long
)
