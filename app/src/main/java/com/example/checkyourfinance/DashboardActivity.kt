package com.example.checkyourfinance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt

class DashboardActivity : AppCompatActivity() {

    private lateinit var textUserName: TextView
    private lateinit var textOverviewSubtitle: TextView
    private lateinit var textBalanceAmount: TextView
    private lateinit var textBalanceTrend: TextView
    private lateinit var textSummaryIncomeAmount: TextView
    private lateinit var textSummaryExpensesAmount: TextView
    private lateinit var textSummarySavingsAmount: TextView
    private lateinit var textSummaryIncomeTrend: TextView
    private lateinit var textSummaryExpensesTrend: TextView
    private lateinit var textSummarySavingsTrend: TextView
    private lateinit var cardSpendingCategories: View
    private lateinit var textSpendingEmpty: TextView
    private lateinit var textRecentEmpty: TextView
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
    private lateinit var textCatFoodAmount: TextView
    private lateinit var textCatFoodPct: TextView
    private lateinit var textCatTransportAmount: TextView
    private lateinit var textCatTransportPct: TextView
    private lateinit var textCatShoppingAmount: TextView
    private lateinit var textCatShoppingPct: TextView
    private lateinit var textCatBillsAmount: TextView
    private lateinit var textCatBillsPct: TextView
    private lateinit var textCatEntertainmentAmount: TextView
    private lateinit var textCatEntertainmentPct: TextView
    private lateinit var progressCatFood: ProgressBar
    private lateinit var progressCatTransport: ProgressBar
    private lateinit var progressCatShopping: ProgressBar
    private lateinit var progressCatBills: ProgressBar
    private lateinit var progressCatEntertainment: ProgressBar

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
        textOverviewSubtitle = findViewById(R.id.text_overview_subtitle)
        textBalanceAmount = findViewById(R.id.text_balance_amount)
        textBalanceTrend = findViewById(R.id.text_balance_trend)
        textSummaryIncomeAmount = findViewById(R.id.text_summary_income_amount)
        textSummaryExpensesAmount = findViewById(R.id.text_summary_expenses_amount)
        textSummarySavingsAmount = findViewById(R.id.text_summary_savings_amount)
        textSummaryIncomeTrend = findViewById(R.id.text_summary_income_trend)
        textSummaryExpensesTrend = findViewById(R.id.text_summary_expenses_trend)
        textSummarySavingsTrend = findViewById(R.id.text_summary_savings_trend)
        cardSpendingCategories = findViewById(R.id.card_spending_categories)
        textSpendingEmpty = findViewById(R.id.text_spending_empty)
        textRecentEmpty = findViewById(R.id.text_recent_empty)
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
        textCatFoodAmount = findViewById(R.id.text_cat_food_amount)
        textCatFoodPct = findViewById(R.id.text_cat_food_pct)
        textCatTransportAmount = findViewById(R.id.text_cat_transport_amount)
        textCatTransportPct = findViewById(R.id.text_cat_transport_pct)
        textCatShoppingAmount = findViewById(R.id.text_cat_shopping_amount)
        textCatShoppingPct = findViewById(R.id.text_cat_shopping_pct)
        textCatBillsAmount = findViewById(R.id.text_cat_bills_amount)
        textCatBillsPct = findViewById(R.id.text_cat_bills_pct)
        textCatEntertainmentAmount = findViewById(R.id.text_cat_entertainment_amount)
        textCatEntertainmentPct = findViewById(R.id.text_cat_entertainment_pct)
        progressCatFood = findViewById(R.id.progress_cat_food)
        progressCatTransport = findViewById(R.id.progress_cat_transport)
        progressCatShopping = findViewById(R.id.progress_cat_shopping)
        progressCatBills = findViewById(R.id.progress_cat_bills)
        progressCatEntertainment = findViewById(R.id.progress_cat_entertainment)

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
            textOverviewSubtitle.setText(R.string.dashboard_demo_subtitle)
            textRecentEmpty.visibility = View.GONE
            textSpendingEmpty.visibility = View.GONE
            cardSpendingCategories.visibility = View.VISIBLE
            showSummaryTrends(true)
            textBalanceTrend.visibility = View.VISIBLE
            showStaticRecentTransactions()
            return
        }

        textUserName.text = session.getUserName().orEmpty()
        textOverviewSubtitle.setText(R.string.dashboard_logged_subtitle)
        showSummaryTrends(false)
        textBalanceTrend.visibility = View.GONE

        lifecycleScope.launch {
            val userId = session.getUserId()
            val transactions = withContext(Dispatchers.IO) {
                app.repository.getExpensesForUser(userId)
            }

            bindSummary(transactions)
            bindSpendingByCategory(transactions)
            val recent = transactions.take(3)
            bindRecentRow(rowRecentTx1, textTx1Merchant, textTx1Amount, textTx1Category, textTx1Date, recent.getOrNull(0))
            bindRecentRow(rowRecentTx2, textTx2Merchant, textTx2Amount, textTx2Category, textTx2Date, recent.getOrNull(1))
            bindRecentRow(rowRecentTx3, textTx3Merchant, textTx3Amount, textTx3Category, textTx3Date, recent.getOrNull(2))
            textRecentEmpty.visibility = if (recent.isEmpty()) View.VISIBLE else View.GONE
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

    private fun bindSummary(transactions: List<ExpenseUiModel>) {
        val income = transactions
            .filter { it.transactionType == TransactionType.INCOME }
            .sumOf { it.amount }
        val expenses = transactions
            .filter { it.transactionType == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val savings = income - expenses

        textSummaryIncomeAmount.text = formatSummaryAmount(income, TransactionType.INCOME)
        textSummaryExpensesAmount.text = formatSummaryAmount(expenses, TransactionType.EXPENSE)
        textSummarySavingsAmount.text = if (savings >= 0) {
            formatSummaryAmount(savings, TransactionType.INCOME)
        } else {
            formatSummaryAmount(abs(savings), TransactionType.EXPENSE)
        }
        textBalanceAmount.text = formatCurrencyAmount(savings)
    }

    private fun bindSpendingByCategory(transactions: List<ExpenseUiModel>) {
        val expenseTransactions = transactions.filter { it.transactionType == TransactionType.EXPENSE }
        if (expenseTransactions.isEmpty()) {
            cardSpendingCategories.visibility = View.GONE
            textSpendingEmpty.visibility = View.VISIBLE
            return
        }

        cardSpendingCategories.visibility = View.VISIBLE
        textSpendingEmpty.visibility = View.GONE

        val grouped = expenseTransactions.groupBy { it.categoryType }
        val totalsByCategory = grouped.mapValues { (_, values) -> values.sumOf { it.amount } }
        val totalExpenses = totalsByCategory.values.sum().takeIf { it > 0 } ?: 1.0

        bindCategory(totalsByCategory[ExpenseCategories.FOOD] ?: 0.0, totalExpenses, textCatFoodAmount, textCatFoodPct, progressCatFood)
        bindCategory(totalsByCategory[ExpenseCategories.TRANSPORT] ?: 0.0, totalExpenses, textCatTransportAmount, textCatTransportPct, progressCatTransport)
        bindCategory(totalsByCategory[ExpenseCategories.SHOPPING] ?: 0.0, totalExpenses, textCatShoppingAmount, textCatShoppingPct, progressCatShopping)
        bindCategory(totalsByCategory[ExpenseCategories.BILLS] ?: 0.0, totalExpenses, textCatBillsAmount, textCatBillsPct, progressCatBills)
        bindCategory(totalsByCategory[ExpenseCategories.ENTERTAINMENT] ?: 0.0, totalExpenses, textCatEntertainmentAmount, textCatEntertainmentPct, progressCatEntertainment)
    }

    private fun bindCategory(
        amount: Double,
        totalExpenses: Double,
        amountView: TextView,
        pctView: TextView,
        progressView: ProgressBar
    ) {
        val percentage = ((amount / totalExpenses) * 100.0).coerceIn(0.0, 100.0)
        amountView.text = formatCurrencyAmount(amount)
        pctView.visibility = View.GONE
        progressView.progress = percentage.roundToInt()
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
        amount.text = formatSignedAmount(expense.amount, expense.transactionType)
        category.text = expense.category
        date.text = expense.date
    }

    private fun showSummaryTrends(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        textSummaryIncomeTrend.visibility = visibility
        textSummaryExpensesTrend.visibility = visibility
        textSummarySavingsTrend.visibility = visibility
    }

    private fun formatSummaryAmount(amount: Double, transactionType: String): String {
        return if (amount == 0.0) {
            formatCurrencyAmount(0.0)
        } else {
            formatSignedAmount(amount, transactionType)
        }
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
        val launchCreateTransaction = View.OnClickListener {
            if (!app.sessionManager.isLoggedIn()) {
                Toast.makeText(this, R.string.toast_login_required_save, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            startActivity(ExpenseFormActivity.createIntent(this))
        }
        findViewById<View>(R.id.nav_add).setOnClickListener(launchCreateTransaction)
        findViewById<FloatingActionButton>(R.id.nav_add_fab).setOnClickListener(launchCreateTransaction)
        findViewById<View>(R.id.nav_budget).setOnClickListener {
            Toast.makeText(this, R.string.toast_budget_view_coming_soon, Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.nav_profile).setOnClickListener {
            Toast.makeText(this, R.string.toast_profile_view_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }
}
