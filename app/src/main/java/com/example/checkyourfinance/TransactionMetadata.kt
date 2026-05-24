package com.example.checkyourfinance

import java.util.Locale
import kotlin.math.abs

object TransactionType {
    const val INCOME = "INCOME"
    const val EXPENSE = "EXPENSE"
}

object PaymentMethod {
    const val CASH = "CASH"
    const val DEBIT_CARD = "DEBIT_CARD"
    const val CREDIT_CARD = "CREDIT_CARD"
    const val BANK_TRANSFER = "BANK_TRANSFER"
    const val OTHER = "OTHER"
}

fun formatSignedAmount(amount: Double, transactionType: String): String {
    val normalized = abs(amount)
    val sign = if (transactionType == TransactionType.INCOME) "+" else "-"
    return String.format(Locale.US, "%s$%.2f", sign, normalized)
}

fun formatCurrencyAmount(amount: Double): String {
    return String.format(Locale.US, "$%.2f", amount)
}
