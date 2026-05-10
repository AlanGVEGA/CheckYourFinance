package com.example.checkyourfinance

data class ExpenseUiModel(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val date: String,
    val categoryType: String,
    val isFavorite: Boolean = false
)

