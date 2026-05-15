package com.example.checkyourfinance

import android.content.Context

object ExpenseCategoryPicker {

    fun categoryFromSpinner(context: Context, position: Int): Pair<String, String>? {
        if (position <= 0) return null
        val labels = context.resources.getStringArray(R.array.expense_form_categories)
        val label = labels.getOrNull(position) ?: return null
        val type = when (position) {
            1 -> ExpenseCategories.FOOD
            2 -> ExpenseCategories.TRANSPORT
            3 -> ExpenseCategories.SHOPPING
            4 -> ExpenseCategories.BILLS
            5 -> ExpenseCategories.ENTERTAINMENT
            6 -> ExpenseCategories.OTHER
            else -> ExpenseCategories.OTHER
        }
        return label to type
    }

    fun spinnerIndexForCategoryLabel(context: Context, categoryLabel: String): Int {
        val labels = context.resources.getStringArray(R.array.expense_form_categories)
        val idx = labels.indexOf(categoryLabel)
        return if (idx >= 0) idx else 0
    }
}
