package com.example.checkyourfinance.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(userId: Int, name: String, email: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, INVALID_USER_ID)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false) && getUserId() != INVALID_USER_ID

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "check_your_finance_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_LOGGED_IN = "logged_in"
        const val INVALID_USER_ID = -1
    }
}
