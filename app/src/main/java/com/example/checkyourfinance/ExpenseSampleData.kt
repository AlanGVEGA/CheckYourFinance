package com.example.checkyourfinance

object ExpenseSampleData {

    val expenses: List<ExpenseUiModel> = listOf(
        ExpenseUiModel(
            id = 1,
            title = "Starbucks",
            category = "Food & Dining",
            amount = 8.90,
            transactionType = TransactionType.EXPENSE,
            date = "Today",
            categoryType = ExpenseCategories.FOOD,
            paymentMethod = PaymentMethod.DEBIT_CARD,
            isFavorite = true,
            description = "Morning coffee."
        ),
        ExpenseUiModel(
            id = 2,
            title = "Amazon",
            category = "Shopping",
            amount = 120.50,
            transactionType = TransactionType.EXPENSE,
            date = "Today",
            categoryType = ExpenseCategories.SHOPPING,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            description = "Household supplies."
        ),
        ExpenseUiModel(
            id = 3,
            title = "Uber",
            category = "Transport",
            amount = 15.40,
            transactionType = TransactionType.EXPENSE,
            date = "Yesterday",
            categoryType = ExpenseCategories.TRANSPORT,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            description = "Ride downtown."
        ),
        ExpenseUiModel(
            id = 4,
            title = "Netflix",
            category = "Entertainment",
            amount = 15.99,
            transactionType = TransactionType.EXPENSE,
            date = "May 8",
            categoryType = ExpenseCategories.ENTERTAINMENT,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            description = "Monthly subscription."
        ),
        ExpenseUiModel(
            id = 5,
            title = "Electricity Bill",
            category = "Bills & Utilities",
            amount = 95.00,
            transactionType = TransactionType.EXPENSE,
            date = "May 6",
            categoryType = ExpenseCategories.BILLS,
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            description = "Utility payment."
        ),
        ExpenseUiModel(
            id = 6,
            title = "Target",
            category = "Shopping",
            amount = 42.30,
            transactionType = TransactionType.EXPENSE,
            date = "May 4",
            categoryType = ExpenseCategories.SHOPPING,
            paymentMethod = PaymentMethod.DEBIT_CARD,
            description = "Groceries and essentials."
        ),
        ExpenseUiModel(
            id = 7,
            title = "McDonald's",
            category = "Food & Dining",
            amount = 12.75,
            transactionType = TransactionType.EXPENSE,
            date = "May 3",
            categoryType = ExpenseCategories.FOOD,
            paymentMethod = PaymentMethod.CASH,
            description = "Quick lunch."
        ),
        ExpenseUiModel(
            id = 8,
            title = "Salary",
            category = "Other",
            amount = 2200.00,
            transactionType = TransactionType.INCOME,
            date = "May 1",
            categoryType = ExpenseCategories.OTHER,
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            description = "Monthly payroll."
        )
    )

    fun findById(id: Int): ExpenseUiModel? = expenses.find { it.id == id }
}
