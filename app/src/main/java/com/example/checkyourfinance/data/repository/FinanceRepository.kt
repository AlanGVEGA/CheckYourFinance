package com.example.checkyourfinance.data.repository

import com.example.checkyourfinance.ExpenseCategories
import com.example.checkyourfinance.ExpenseUiModel
import com.example.checkyourfinance.data.local.ExpenseDao
import com.example.checkyourfinance.data.local.UserDao
import com.example.checkyourfinance.data.model.ExpenseEntity
import com.example.checkyourfinance.data.model.UserEntity

class FinanceRepository(
    private val userDao: UserDao,
    private val expenseDao: ExpenseDao
) {

    suspend fun countUsersByEmail(email: String): Int = userDao.countUsersByEmail(email)

    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    suspend fun getUserByEmailAndPasswordHash(email: String, passwordHash: String): UserEntity? =
        userDao.getUserByEmailAndPasswordHash(email, passwordHash)

    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(userId: Int, expenseId: Int) =
        expenseDao.deleteExpense(userId, expenseId)

    suspend fun getExpense(userId: Int, expenseId: Int): ExpenseEntity? =
        expenseDao.getExpenseById(userId, expenseId)

    suspend fun getExpenseAsUi(userId: Int, expenseId: Int): ExpenseUiModel? =
        getExpense(userId, expenseId)?.toUiModel()

    suspend fun getExpensesForUser(userId: Int): List<ExpenseUiModel> =
        expenseDao.getExpensesByUserId(userId).map { it.toUiModel() }

    suspend fun getExpensesForUserFiltered(userId: Int, categoryType: String): List<ExpenseUiModel> {
        if (categoryType == ExpenseCategories.ALL) {
            return getExpensesForUser(userId)
        }
        return expenseDao.getExpensesByUserIdAndCategory(userId, categoryType).map { it.toUiModel() }
    }

    suspend fun getTotalSpent(userId: Int): Double = expenseDao.getTotalSpentByUserId(userId)

    suspend fun getExpenseCount(userId: Int): Int = expenseDao.getExpenseCountByUserId(userId)

    suspend fun updateFavorite(userId: Int, expenseId: Int, favorite: Boolean) =
        expenseDao.updateFavorite(userId, expenseId, favorite)

    private fun ExpenseEntity.toUiModel(): ExpenseUiModel = ExpenseUiModel(
        id = id,
        title = title,
        category = category,
        amount = amount,
        transactionType = transactionType,
        date = date,
        categoryType = categoryType,
        paymentMethod = paymentMethod,
        isFavorite = isFavorite,
        description = description
    )
}
