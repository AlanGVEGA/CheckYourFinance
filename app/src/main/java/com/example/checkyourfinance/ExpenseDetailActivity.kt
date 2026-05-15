package com.example.checkyourfinance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseDetailActivity : AppCompatActivity() {

    private var expenseId: Int = -1
    private var isFavoriteLocal: Boolean = false
    private var boundExpense: ExpenseUiModel? = null

    private lateinit var buttonBack: ImageButton
    private lateinit var categoryIndicator: View
    private lateinit var textExpenseTitle: TextView
    private lateinit var textAmount: TextView
    private lateinit var textCategory: TextView
    private lateinit var textDate: TextView
    private lateinit var textDescription: TextView
    private lateinit var imageFavoriteIcon: ImageView
    private lateinit var textFavoriteStatus: TextView
    private lateinit var textTransactionId: TextView
    private lateinit var textCategoryType: TextView
    private lateinit var buttonEdit: MaterialButton
    private lateinit var buttonDelete: MaterialButton
    private lateinit var buttonToggleFavorite: MaterialButton
    private lateinit var buttonShare: MaterialButton

    private val app: CheckYourFinanceApplication
        get() = application as CheckYourFinanceApplication

    private val editFormLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val model = withContext(Dispatchers.IO) { resolveExpense(expenseId) }
                if (model == null) {
                    setResult(Activity.RESULT_OK)
                    finish()
                    return@launch
                }
                boundExpense = model
                bindFromModel(model)
                setResult(Activity.RESULT_OK)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.expense_detail_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)

        buttonBack.setOnClickListener { finish() }

        setActionButtonsEnabled(false)

        lifecycleScope.launch {
            val expense = withContext(Dispatchers.IO) { resolveExpense(expenseId) }
            if (expense == null) {
                Toast.makeText(this@ExpenseDetailActivity, R.string.toast_expense_not_found, Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            boundExpense = expense
            bindFromModel(expense)
            setActionButtonsEnabled(true)
            wireActions()
        }
    }

    private fun setActionButtonsEnabled(enabled: Boolean) {
        buttonEdit.isEnabled = enabled
        buttonDelete.isEnabled = enabled
        buttonToggleFavorite.isEnabled = enabled
        buttonShare.isEnabled = enabled
    }

    private suspend fun resolveExpense(id: Int): ExpenseUiModel? {
        val session = app.sessionManager
        if (session.isLoggedIn()) {
            return app.repository.getExpenseAsUi(session.getUserId(), id)
        }
        return ExpenseSampleData.findById(id)
    }

    private fun wireActions() {
        buttonEdit.setOnClickListener {
            editFormLauncher.launch(ExpenseFormActivity.editIntent(this, expenseId))
        }

        buttonDelete.setOnClickListener { showDeleteConfirmation() }

        buttonToggleFavorite.setOnClickListener {
            isFavoriteLocal = !isFavoriteLocal
            refreshFavoriteUi()
            persistFavoriteIfPossible()
            Toast.makeText(this, R.string.toast_favorite_status_updated, Toast.LENGTH_SHORT).show()
        }

        buttonShare.setOnClickListener { shareCurrentExpense() }
    }

    private fun persistFavoriteIfPossible() {
        if (!app.sessionManager.isLoggedIn()) return
        val userId = app.sessionManager.getUserId()
        lifecycleScope.launch(Dispatchers.IO) {
            app.repository.updateFavorite(userId, expenseId, isFavoriteLocal)
        }
    }

    private fun bindViews() {
        buttonBack = findViewById(R.id.button_back)
        categoryIndicator = findViewById(R.id.view_detail_category_indicator)
        textExpenseTitle = findViewById(R.id.text_detail_expense_title)
        textAmount = findViewById(R.id.text_detail_amount)
        textCategory = findViewById(R.id.text_detail_category)
        textDate = findViewById(R.id.text_detail_date)
        textDescription = findViewById(R.id.text_detail_description)
        imageFavoriteIcon = findViewById(R.id.image_detail_favorite_icon)
        textFavoriteStatus = findViewById(R.id.text_detail_favorite_status)
        textTransactionId = findViewById(R.id.text_detail_transaction_id)
        textCategoryType = findViewById(R.id.text_detail_category_type)
        buttonEdit = findViewById(R.id.button_edit_expense)
        buttonDelete = findViewById(R.id.button_delete_expense)
        buttonToggleFavorite = findViewById(R.id.button_toggle_favorite)
        buttonShare = findViewById(R.id.button_share_expense)
    }

    private fun bindFromModel(expense: ExpenseUiModel) {
        isFavoriteLocal = expense.isFavorite
        textExpenseTitle.text = expense.title
        textAmount.text = getString(R.string.expense_amount_format, expense.amount)
        textCategory.text = expense.category
        textDate.text = expense.date
        textDescription.text = expense.description.ifBlank {
            getString(R.string.expense_detail_description_empty)
        }
        textTransactionId.text = getString(R.string.expense_detail_transaction_id_format, expense.id)
        textCategoryType.text = categoryTypeLabel(expense.categoryType)
        applyCategoryIndicatorColor(expense.categoryType)
        refreshFavoriteUi()
        boundExpense = expense
    }

    private fun refreshFavoriteUi() {
        textFavoriteStatus.setText(
            if (isFavoriteLocal) {
                R.string.expense_detail_favorite_marked
            } else {
                R.string.expense_detail_favorite_not
            }
        )
        val iconRes = if (isFavoriteLocal) R.drawable.ic_star else R.drawable.ic_star_border
        imageFavoriteIcon.setImageResource(iconRes)
        buttonToggleFavorite.setIconResource(iconRes)
    }

    private fun categoryTypeLabel(categoryType: String): String {
        val resId = when (categoryType) {
            ExpenseCategories.FOOD -> R.string.chip_food
            ExpenseCategories.TRANSPORT -> R.string.chip_transport
            ExpenseCategories.SHOPPING -> R.string.chip_shopping
            ExpenseCategories.BILLS -> R.string.chip_bills
            ExpenseCategories.ENTERTAINMENT -> R.string.chip_entertainment
            ExpenseCategories.OTHER -> R.string.category_other
            else -> R.string.category_other
        }
        return getString(resId)
    }

    private fun applyCategoryIndicatorColor(categoryType: String) {
        val colorRes = when (categoryType) {
            ExpenseCategories.FOOD -> R.color.category_food
            ExpenseCategories.TRANSPORT -> R.color.category_transport
            ExpenseCategories.SHOPPING -> R.color.category_shopping
            ExpenseCategories.BILLS -> R.color.category_bills
            ExpenseCategories.ENTERTAINMENT -> R.color.category_entertainment
            ExpenseCategories.OTHER -> R.color.border_soft
            else -> R.color.border_soft
        }
        (categoryIndicator.background as? GradientDrawable)?.setColor(
            ContextCompat.getColor(this, colorRes)
        )
    }

    private fun showDeleteConfirmation() {
        if (!app.sessionManager.isLoggedIn()) {
            Toast.makeText(this, R.string.toast_login_required_delete, Toast.LENGTH_SHORT).show()
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.expense_delete_dialog_title)
            .setMessage(R.string.expense_delete_dialog_message)
            .setPositiveButton(R.string.expense_delete_dialog_positive) { _, _ ->
                val userId = app.sessionManager.getUserId()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        app.repository.deleteExpense(userId, expenseId)
                    }
                    Toast.makeText(this@ExpenseDetailActivity, R.string.toast_expense_deleted_success, Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            .setNegativeButton(R.string.expense_delete_dialog_negative, null)
            .show()
    }

    private fun shareCurrentExpense() {
        val expense = boundExpense ?: return
        val amountText = getString(R.string.expense_amount_format, expense.amount)
        val descriptionText = expense.description.ifBlank {
            getString(R.string.expense_share_description_empty)
        }
        val shareBody = getString(
            R.string.expense_share_text_template,
            expense.title,
            amountText,
            expense.category,
            expense.date,
            descriptionText
        )
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        startActivity(
            Intent.createChooser(sendIntent, getString(R.string.expense_share_chooser_title))
        )
    }

    companion object {
        const val EXTRA_EXPENSE_ID = "extra_expense_id"

        fun createIntent(context: Context, expenseId: Int): Intent =
            Intent(context, ExpenseDetailActivity::class.java).apply {
                putExtra(EXTRA_EXPENSE_ID, expenseId)
            }
    }
}
