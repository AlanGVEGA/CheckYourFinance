package com.example.checkyourfinance

object ExpenseSampleData {

    val expenses: List<ExpenseUiModel> = listOf(
        ExpenseUiModel(
            id = 1,
            title = "Starbucks",
            category = "Food & Dining",
            amount = 8.90,
            date = "Today",
            categoryType = ExpenseCategories.FOOD,
            isFavorite = true,
            description = "Morning coffee."
        ),
        ExpenseUiModel(
            id = 2,
            title = "Amazon",
            category = "Shopping",
            amount = 120.50,
            date = "Today",
            categoryType = ExpenseCategories.SHOPPING,
            description = "Household supplies."
        ),
        ExpenseUiModel(
            id = 3,
            title = "Uber",
            category = "Transport",
            amount = 15.40,
            date = "Yesterday",
            categoryType = ExpenseCategories.TRANSPORT,
            description = "Ride downtown."
        ),
        ExpenseUiModel(
            id = 4,
            title = "Netflix",
            category = "Entertainment",
            amount = 15.99,
            date = "May 8",
            categoryType = ExpenseCategories.ENTERTAINMENT,
            description = "Monthly subscription."
        ),
        ExpenseUiModel(
            id = 5,
            title = "Electricity Bill",
            category = "Bills & Utilities",
            amount = 95.00,
            date = "May 6",
            categoryType = ExpenseCategories.BILLS,
            description = "Utility payment."
        ),
        ExpenseUiModel(
            id = 6,
            title = "Target",
            category = "Shopping",
            amount = 42.30,
            date = "May 4",
            categoryType = ExpenseCategories.SHOPPING,
            description = "Groceries and essentials."
        ),
        ExpenseUiModel(
            id = 7,
            title = "McDonald's",
            category = "Food & Dining",
            amount = 12.75,
            date = "May 3",
            categoryType = ExpenseCategories.FOOD,
            description = "Quick lunch."
        )
    )

    fun findById(id: Int): ExpenseUiModel? = expenses.find { it.id == id }
}
