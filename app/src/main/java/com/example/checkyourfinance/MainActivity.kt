package com.example.checkyourfinance

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.checkyourfinance.data.local.PasswordHasher
import com.example.checkyourfinance.data.model.UserEntity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var isLoginMode: Boolean = true

    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var inputFullName: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var inputConfirmPassword: TextInputEditText

    private val app: CheckYourFinanceApplication
        get() = application as CheckYourFinanceApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (app.sessionManager.isLoggedIn()) {
            navigateToDashboard()
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        applyAuthModeUi()

        findViewById<View>(R.id.button_primary_action).setOnClickListener {
            if (isLoginMode) {
                attemptLogin()
            } else {
                attemptRegister()
            }
        }

        findViewById<View>(R.id.text_footer_action).setOnClickListener {
            isLoginMode = !isLoginMode
            clearFieldErrors()
            applyAuthModeUi()
        }

        findViewById<View>(R.id.button_forgot_password).setOnClickListener {
            Toast.makeText(this, R.string.toast_forgot_password_coming_soon, Toast.LENGTH_SHORT)
                .show()
        }

        findViewById<View>(R.id.button_continue_google).setOnClickListener {
            Toast.makeText(this, R.string.toast_google_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.button_continue_apple).setOnClickListener {
            Toast.makeText(this, R.string.toast_apple_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.button_continue_guest).setOnClickListener {
            navigateToDashboard()
        }
    }

    private fun navigateToDashboard() {
        startActivity(
            Intent(this, DashboardActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
    }

    private fun attemptLogin() {
        clearFieldErrors()
        if (!validateLoginFields()) return

        val email = inputEmail.text?.toString()?.trim().orEmpty()
        val password = inputPassword.text?.toString().orEmpty()
        val hash = PasswordHasher.hashPassword(password)

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                app.repository.getUserByEmailAndPasswordHash(email, hash)
            }
            if (user == null) {
                tilPassword.error = getString(R.string.error_login_failed)
                Toast.makeText(this@MainActivity, R.string.error_login_failed, Toast.LENGTH_SHORT).show()
            } else {
                app.sessionManager.saveUserSession(user.id, user.name, user.email)
                navigateToDashboard()
            }
        }
    }

    private fun attemptRegister() {
        clearFieldErrors()
        if (!validateRegisterFields()) return

        val fullName = inputFullName.text?.toString()?.trim().orEmpty()
        val email = inputEmail.text?.toString()?.trim().orEmpty()
        val password = inputPassword.text?.toString().orEmpty()

        lifecycleScope.launch {
            val existing = withContext(Dispatchers.IO) {
                app.repository.countUsersByEmail(email)
            }
            if (existing > 0) {
                tilEmail.error = getString(R.string.error_email_exists)
                return@launch
            }

            val entity = UserEntity(
                name = fullName,
                email = email,
                passwordHash = PasswordHasher.hashPassword(password),
                createdAt = System.currentTimeMillis()
            )
            val newId = withContext(Dispatchers.IO) {
                app.repository.insertUser(entity).toInt()
            }
            app.sessionManager.saveUserSession(newId, fullName, email)
            navigateToDashboard()
        }
    }

    private fun bindViews() {
        tilFullName = findViewById(R.id.til_full_name)
        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)
        tilConfirmPassword = findViewById(R.id.til_confirm_password)

        inputFullName = findViewById(R.id.input_full_name)
        inputEmail = findViewById(R.id.input_email)
        inputPassword = findViewById(R.id.input_password)
        inputConfirmPassword = findViewById(R.id.input_confirm_password)
    }

    private fun applyAuthModeUi() {
        findViewById<TextView>(R.id.text_screen_title).setText(
            if (isLoginMode) R.string.login_title else R.string.register_title
        )
        findViewById<TextView>(R.id.text_screen_subtitle).setText(
            if (isLoginMode) R.string.login_subtitle else R.string.register_subtitle
        )
        findViewById<TextView>(R.id.button_primary_action).setText(
            if (isLoginMode) R.string.action_login else R.string.action_sign_up
        )

        val registerOnlyVisibility = if (isLoginMode) View.GONE else View.VISIBLE
        tilFullName.visibility = registerOnlyVisibility
        tilConfirmPassword.visibility = registerOnlyVisibility

        findViewById<View>(R.id.button_forgot_password).visibility =
            if (isLoginMode) View.VISIBLE else View.GONE
        findViewById<View>(R.id.button_continue_guest).visibility =
            if (isLoginMode) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.text_footer_prompt).setText(
            if (isLoginMode) R.string.footer_need_account else R.string.footer_have_account
        )
        findViewById<TextView>(R.id.text_footer_action).setText(
            if (isLoginMode) R.string.footer_sign_up else R.string.footer_log_in
        )

        if (isLoginMode) {
            tilFullName.editText?.text?.clear()
            tilConfirmPassword.editText?.text?.clear()
        }
    }

    private fun clearFieldErrors() {
        tilFullName.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null
    }

    private fun validateLoginFields(): Boolean {
        val email = inputEmail.text?.toString()?.trim().orEmpty()
        val password = inputPassword.text?.toString().orEmpty()

        var valid = true

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_field_required)
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_email_invalid)
            valid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_field_required)
            valid = false
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_short)
            valid = false
        }

        return valid
    }

    private fun validateRegisterFields(): Boolean {
        val fullName = inputFullName.text?.toString()?.trim().orEmpty()
        val email = inputEmail.text?.toString()?.trim().orEmpty()
        val password = inputPassword.text?.toString().orEmpty()
        val confirm = inputConfirmPassword.text?.toString().orEmpty()

        var valid = true

        if (fullName.isEmpty()) {
            tilFullName.error = getString(R.string.error_field_required)
            valid = false
        }

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_field_required)
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_email_invalid)
            valid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_field_required)
            valid = false
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_short)
            valid = false
        }

        if (confirm.isEmpty()) {
            tilConfirmPassword.error = getString(R.string.error_field_required)
            valid = false
        } else if (confirm != password) {
            tilConfirmPassword.error = getString(R.string.error_confirm_password_mismatch)
            valid = false
        }

        return valid
    }
}
