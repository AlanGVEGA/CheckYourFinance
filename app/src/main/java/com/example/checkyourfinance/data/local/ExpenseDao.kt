package com.example.checkyourfinance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.checkyourfinance.data.model.ExpenseEntity

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :expenseId AND userId = :userId")
    suspend fun deleteExpense(userId: Int, expenseId: Int)

    @Query("SELECT * FROM expenses WHERE id = :expenseId AND userId = :userId LIMIT 1")
    suspend fun getExpenseById(userId: Int, expenseId: Int): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getExpensesByUserId(userId: Int): List<ExpenseEntity>

    @Query(
        "SELECT * FROM expenses WHERE userId = :userId AND categoryType = :category " +
            "ORDER BY createdAt DESC"
    )
    suspend fun getExpensesByUserIdAndCategory(userId: Int, category: String): List<ExpenseEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE userId = :userId")
    suspend fun getTotalSpentByUserId(userId: Int): Double

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId")
    suspend fun getExpenseCountByUserId(userId: Int): Int

    @Query(
        "UPDATE expenses SET isFavorite = :favorite WHERE id = :expenseId AND userId = :userId"
    )
    suspend fun updateFavorite(userId: Int, expenseId: Int, favorite: Boolean)
}
