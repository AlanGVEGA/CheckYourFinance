package com.example.checkyourfinance

data class ExpenseUiModel(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val transactionType: String = TransactionType.EXPENSE,
    val date: String,
    val categoryType: String,
    val paymentMethod: String = PaymentMethod.OTHER,
    val isFavorite: Boolean = false,
    val description: String = ""
)

