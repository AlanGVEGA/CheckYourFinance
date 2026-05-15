package com.example.checkyourfinance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.checkyourfinance.data.model.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query(
        "SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1"
    )
    suspend fun getUserByEmailAndPasswordHash(email: String, passwordHash: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun countUsersByEmail(email: String): Int
}
