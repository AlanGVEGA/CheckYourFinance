package com.example.checkyourfinance

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            if (isLoginMode) validateAndToastLogin() else validateAndToastRegister()
        }

        findViewById<View>(R.id.text_footer_action).setOnClickListener {
            isLoginMode = !isLoginMode
            clearFieldErrors()
            applyAuthModeUi()
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

    private fun validateAndToastLogin(): Boolean {
        clearFieldErrors()

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

        if (valid) {
            Toast.makeText(this, R.string.toast_login_validation_ok, Toast.LENGTH_SHORT).show()
        }
        return valid
    }

    private fun validateAndToastRegister(): Boolean {
        clearFieldErrors()

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

        if (valid) {
            Toast.makeText(this, R.string.toast_register_validation_ok, Toast.LENGTH_SHORT).show()
        }
        return valid
    }
}
