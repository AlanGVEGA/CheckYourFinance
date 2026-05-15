package com.example.checkyourfinance

import android.app.Application
import com.example.checkyourfinance.data.local.AppDatabase
import com.example.checkyourfinance.data.repository.FinanceRepository
import com.example.checkyourfinance.session.SessionManager

class CheckYourFinanceApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: FinanceRepository
        private set

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        sessionManager = SessionManager(this)
        repository = FinanceRepository(database.userDao(), database.expenseDao())
    }
}
