package com.example.checkyourfinance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private lateinit var textUserName: TextView
    private lateinit var textSummaryExpensesAmount: TextView
    private lateinit var rowRecentTx1: View
    private lateinit var rowRecentTx2: View
    private lateinit var rowRecentTx3: View
    private lateinit var textTx1Merchant: TextView
    private lateinit var textTx1Amount: TextView
    private lateinit var textTx1Category: TextView
    private lateinit var textTx1Date: TextView
    private lateinit var textTx2Merchant: TextView
    private lateinit var textTx2Amount: TextView
    private lateinit var textTx2Category: TextView
    private lateinit var textTx2Date: TextView
    private lateinit var textTx3Merchant: TextView
    private lateinit var textTx3Amount: TextView
    private lateinit var textTx3Category: TextView
    private lateinit var textTx3Date: TextView

    private val app: CheckYourFinanceApplication
        get() = application as CheckYourFinanceApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textUserName = findViewById(R.id.text_user_name)
        textSummaryExpensesAmount = findViewById(R.id.text_summary_expenses_amount)
        rowRecentTx1 = findViewById(R.id.row_recent_tx1)
        rowRecentTx2 = findViewById(R.id.row_recent_tx2)
        rowRecentTx3 = findViewById(R.id.row_recent_tx3)
        textTx1Merchant = findViewById(R.id.text_tx1_merchant)
        textTx1Amount = findViewById(R.id.text_tx1_amount)
        textTx1Category = findViewById(R.id.text_tx1_category)
        textTx1Date = findViewById(R.id.text_tx1_date)
        textTx2Merchant = findViewById(R.id.text_tx2_merchant)
        textTx2Amount = findViewById(R.id.text_tx2_amount)
        textTx2Category = findViewById(R.id.text_tx2_category)
        textTx2Date = findViewById(R.id.text_tx2_date)
        textTx3Merchant = findViewById(R.id.text_tx3_merchant)
        textTx3Amount = findViewById(R.id.text_tx3_amount)
        textTx3Category = findViewById(R.id.text_tx3_category)
        textTx3Date = findViewById(R.id.text_tx3_date)

        bindClicks()
    }

    override fun onResume() {
        super.onResume()
        refreshDashboardData()
    }

    private fun refreshDashboardData() {
        val session = app.sessionManager
        if (!session.isLoggedIn()) {
            textUserName.setText(R.string.dashboard_user_name_placeholder)
            textSummaryExpensesAmount.setText(R.string.dashboard_expenses_amount)
            showStaticRecentTransactions()
            return
        }

        textUserName.text = session.getUserName().orEmpty()

        lifecycleScope.launch {
            val userId = session.getUserId()
            val expenses = withContext(Dispatchers.IO) {
                app.repository.getExpensesForUser(userId)
            }
            if (expenses.isEmpty()) {
                textSummaryExpensesAmount.setText(R.string.dashboard_expenses_amount)
                showStaticRecentTransactions()
                return@launch
            }

            val total = expenses.sumOf { it.amount }
            textSummaryExpensesAmount.text = getString(R.string.expense_amount_format, total)

            val recent = expenses.take(3)
            bindRecentRow(rowRecentTx1, textTx1Merchant, textTx1Amount, textTx1Category, textTx1Date, recent.getOrNull(0))
            bindRecentRow(rowRecentTx2, textTx2Merchant, textTx2Amount, textTx2Category, textTx2Date, recent.getOrNull(1))
            bindRecentRow(rowRecentTx3, textTx3Merchant, textTx3Amount, textTx3Category, textTx3Date, recent.getOrNull(2))
        }
    }

    private fun showStaticRecentTransactions() {
        rowRecentTx1.visibility = View.VISIBLE
        rowRecentTx2.visibility = View.VISIBLE
        rowRecentTx3.visibility = View.VISIBLE
        textTx1Merchant.setText(R.string.tx_amazon_merchant)
        textTx1Amount.setText(R.string.tx_amount_amazon)
        textTx1Category.setText(R.string.category_shopping)
        textTx1Date.setText(R.string.tx_today)
        textTx2Merchant.setText(R.string.tx_starbucks_merchant)
        textTx2Amount.setText(R.string.tx_amount_starbucks)
        textTx2Category.setText(R.string.category_food_dining)
        textTx2Date.setText(R.string.tx_today)
        textTx3Merchant.setText(R.string.tx_uber_merchant)
        textTx3Amount.setText(R.string.tx_amount_uber)
        textTx3Category.setText(R.string.category_transport)
        textTx3Date.setText(R.string.tx_yesterday)
    }

    private fun bindRecentRow(
        row: View,
        merchant: TextView,
        amount: TextView,
        category: TextView,
        date: TextView,
        expense: ExpenseUiModel?
    ) {
        if (expense == null) {
            row.visibility = View.GONE
            return
        }
        row.visibility = View.VISIBLE
        merchant.text = expense.title
        amount.text = getString(R.string.expense_amount_format, expense.amount)
        category.text = expense.category
        date.text = expense.date
    }

    private fun bindClicks() {
        findViewById<View>(R.id.button_notifications).setOnClickListener {
            Toast.makeText(this, R.string.toast_notifications_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.button_profile).setOnClickListener {
            if (!app.sessionManager.isLoggedIn()) {
                Toast.makeText(this, R.string.toast_profile_settings_coming_soon, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_dialog_positive) { _, _ ->
                    app.sessionManager.clearSession()
                    Toast.makeText(this, R.string.toast_logged_out, Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    )
                    finish()
                }
                .setNegativeButton(R.string.logout_dialog_negative, null)
                .show()
        }

        findViewById<View>(R.id.text_spending_view_all).setOnClickListener {
            Toast.makeText(this, R.string.toast_categories_view_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.text_recent_view_all).setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }

        findViewById<View>(R.id.nav_home).setOnClickListener {
            // Already on Home (active)
        }
        findViewById<View>(R.id.nav_transactions).setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
        findViewById<View>(R.id.nav_add).setOnClickListener {
            if (!app.sessionManager.isLoggedIn()) {
                Toast.makeText(this, R.string.toast_login_required_save, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(ExpenseFormActivity.createIntent(this))
        }
        findViewById<View>(R.id.nav_budget).setOnClickListener {
            Toast.makeText(this, R.string.toast_budget_view_coming_soon, Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.nav_profile).setOnClickListener {
            Toast.makeText(this, R.string.toast_profile_view_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }
}
