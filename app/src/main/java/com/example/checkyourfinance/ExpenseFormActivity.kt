package com.example.checkyourfinance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.checkyourfinance.data.model.ExpenseEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseFormActivity : AppCompatActivity() {

    private lateinit var textScreenTitle: TextView
    private lateinit var textScreenSubtitle: TextView
    private lateinit var tilTitle: TextInputLayout
    private lateinit var inputTitle: TextInputEditText
    private lateinit var tilAmount: TextInputLayout
    private lateinit var inputAmount: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var textCategoryError: TextView
    private lateinit var spinnerTransactionType: Spinner
    private lateinit var textTransactionTypeError: TextView
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var textPaymentMethodError: TextView
    private lateinit var tilDate: TextInputLayout
    private lateinit var inputDate: TextInputEditText
    private lateinit var tilDescription: TextInputLayout
    private lateinit var inputDescription: TextInputEditText
    private lateinit var buttonSave: MaterialButton

    private var isEditMode: Boolean = false
    private var expenseId: Int = -1

    private val app: CheckYourFinanceApplication
        get() = application as CheckYourFinanceApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_form)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.expense_form_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        isEditMode = intent.getBooleanExtra(EXTRA_EDIT_MODE, false)
        expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)

        setupCategorySpinner()
        setupTransactionTypeSpinner()
        setupPaymentMethodSpinner()
        applyModeUi()
        loadExistingExpense()

        findViewById<View>(R.id.button_back).setOnClickListener { finish() }

        buttonSave.setOnClickListener {
            attemptSave()
        }
    }

    private fun loadExistingExpense() {
        if (!isEditMode || expenseId < 0) return

        lifecycleScope.launch {
            val session = app.sessionManager
            val loaded = withContext(Dispatchers.IO) {
                if (session.isLoggedIn()) {
                    app.repository.getExpenseAsUi(session.getUserId(), expenseId)
                } else {
                    null
                }
            } ?: ExpenseSampleData.findById(expenseId)

            if (loaded != null) {
                populateFromExpense(loaded)
            }
        }
    }

    private fun bindViews() {
        textScreenTitle = findViewById(R.id.text_form_title)
        textScreenSubtitle = findViewById(R.id.text_form_subtitle)
        tilTitle = findViewById(R.id.til_expense_title)
        inputTitle = findViewById(R.id.input_expense_title)
        tilAmount = findViewById(R.id.til_expense_amount)
        inputAmount = findViewById(R.id.input_expense_amount)
        spinnerCategory = findViewById(R.id.spinner_category)
        textCategoryError = findViewById(R.id.text_category_error)
        spinnerTransactionType = findViewById(R.id.spinner_transaction_type)
        textTransactionTypeError = findViewById(R.id.text_transaction_type_error)
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_method)
        textPaymentMethodError = findViewById(R.id.text_payment_method_error)
        tilDate = findViewById(R.id.til_expense_date)
        inputDate = findViewById(R.id.input_expense_date)
        tilDescription = findViewById(R.id.til_expense_description)
        inputDescription = findViewById(R.id.input_expense_description)
        buttonSave = findViewById(R.id.button_save_expense)
    }

    private fun setupCategorySpinner() {
        val labels = resources.getStringArray(R.array.expense_form_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        spinnerCategory.onItemSelectedListener = hideErrorOnSelection(textCategoryError)
    }

    private fun setupTransactionTypeSpinner() {
        val labels = resources.getStringArray(R.array.expense_form_transaction_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTransactionType.adapter = adapter
        spinnerTransactionType.onItemSelectedListener = hideErrorOnSelection(textTransactionTypeError)
    }

    private fun setupPaymentMethodSpinner() {
        val labels = resources.getStringArray(R.array.expense_form_payment_methods)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = adapter
        spinnerPaymentMethod.onItemSelectedListener = hideErrorOnSelection(textPaymentMethodError)
    }

    private fun hideErrorOnSelection(errorView: TextView): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    errorView.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no-op
            }
        }
    }

    private fun applyModeUi() {
        if (isEditMode) {
            textScreenTitle.setText(R.string.expense_form_title_edit)
            textScreenSubtitle.setText(R.string.expense_form_subtitle_edit)
            buttonSave.setText(R.string.expense_form_action_update)
        } else {
            textScreenTitle.setText(R.string.expense_form_title_add)
            textScreenSubtitle.setText(R.string.expense_form_subtitle_add)
            buttonSave.setText(R.string.expense_form_action_save)
        }
    }

    private fun populateFromExpense(expense: ExpenseUiModel) {
        inputTitle.setText(expense.title)
        inputAmount.setText(formatAmountForInput(expense.amount))
        inputDate.setText(expense.date)
        inputDescription.setText(expense.description)

        val index = ExpenseCategoryPicker.spinnerIndexForCategoryLabel(this, expense.category)
        if (index > 0) {
            spinnerCategory.setSelection(index)
        }
        spinnerTransactionType.setSelection(transactionTypeSpinnerIndex(expense.transactionType))
        spinnerPaymentMethod.setSelection(paymentMethodSpinnerIndex(expense.paymentMethod))
    }

    private fun formatAmountForInput(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format("%.2f", amount)
        }
    }

    private fun attemptSave() {
        if (!validateAndSubmit()) return

        if (!app.sessionManager.isLoggedIn()) {
            Toast.makeText(this, R.string.toast_login_required_save, Toast.LENGTH_SHORT).show()
            return
        }

        val title = inputTitle.text?.toString()?.trim().orEmpty()
        val amount = parseAmount(inputAmount.text?.toString()?.trim().orEmpty()) ?: return
        val date = inputDate.text?.toString()?.trim().orEmpty()
        val description = inputDescription.text?.toString()?.trim().orEmpty()
        val categoryIndex = spinnerCategory.selectedItemPosition
        val categoryPair = ExpenseCategoryPicker.categoryFromSpinner(this, categoryIndex) ?: return
        val (categoryLabel, categoryType) = categoryPair
        val transactionType = transactionTypeFromSpinner(spinnerTransactionType.selectedItemPosition)
            ?: return
        val paymentMethod = paymentMethodFromSpinner(spinnerPaymentMethod.selectedItemPosition)
            ?: return
        val userId = app.sessionManager.getUserId()
        val now = System.currentTimeMillis()

        lifecycleScope.launch {
            if (isEditMode && expenseId >= 0) {
                val existing = withContext(Dispatchers.IO) {
                    app.repository.getExpense(userId, expenseId)
                }
                if (existing != null) {
                    val updated = existing.copy(
                        title = title,
                        amount = amount,
                        transactionType = transactionType,
                        category = categoryLabel,
                        categoryType = categoryType,
                        paymentMethod = paymentMethod,
                        date = date,
                        description = description
                    )
                    withContext(Dispatchers.IO) { app.repository.updateExpense(updated) }
                } else {
                    val newEntity = ExpenseEntity(
                        id = 0,
                        userId = userId,
                        title = title,
                        amount = amount,
                        transactionType = transactionType,
                        category = categoryLabel,
                        categoryType = categoryType,
                        paymentMethod = paymentMethod,
                        date = date,
                        description = description,
                        isFavorite = false,
                        createdAt = now
                    )
                    withContext(Dispatchers.IO) { app.repository.insertExpense(newEntity) }
                }
            } else {
                val newEntity = ExpenseEntity(
                    id = 0,
                    userId = userId,
                    title = title,
                    amount = amount,
                    transactionType = transactionType,
                    category = categoryLabel,
                    categoryType = categoryType,
                    paymentMethod = paymentMethod,
                    date = date,
                    description = description,
                    isFavorite = false,
                    createdAt = now
                )
                withContext(Dispatchers.IO) { app.repository.insertExpense(newEntity) }
            }

            val message = if (isEditMode) {
                R.string.toast_expense_updated_success
            } else {
                R.string.toast_expense_created_success
            }
            Toast.makeText(this@ExpenseFormActivity, message, Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun validateAndSubmit(): Boolean {
        clearFieldErrors()

        val title = inputTitle.text?.toString()?.trim().orEmpty()
        val amountRaw = inputAmount.text?.toString()?.trim().orEmpty()
        val date = inputDate.text?.toString()?.trim().orEmpty()
        val categoryIndex = spinnerCategory.selectedItemPosition
        val transactionTypeIndex = spinnerTransactionType.selectedItemPosition
        val paymentMethodIndex = spinnerPaymentMethod.selectedItemPosition

        var ok = true

        if (title.isEmpty()) {
            tilTitle.error = getString(R.string.error_field_required)
            ok = false
        }

        if (amountRaw.isEmpty()) {
            tilAmount.error = getString(R.string.error_field_required)
            ok = false
        } else {
            val amount = parseAmount(amountRaw)
            if (amount == null) {
                tilAmount.error = getString(R.string.error_amount_invalid)
                ok = false
            } else if (amount <= 0) {
                tilAmount.error = getString(R.string.error_amount_must_be_positive)
                ok = false
            }
        }

        if (categoryIndex <= 0) {
            textCategoryError.visibility = View.VISIBLE
            textCategoryError.text = getString(R.string.error_expense_category_required)
            ok = false
        }

        if (transactionTypeIndex <= 0) {
            textTransactionTypeError.visibility = View.VISIBLE
            textTransactionTypeError.text = getString(R.string.error_expense_transaction_type_required)
            ok = false
        }

        if (paymentMethodIndex <= 0) {
            textPaymentMethodError.visibility = View.VISIBLE
            textPaymentMethodError.text = getString(R.string.error_expense_payment_method_required)
            ok = false
        }

        if (date.isEmpty()) {
            tilDate.error = getString(R.string.error_field_required)
            ok = false
        }

        return ok
    }

    private fun clearFieldErrors() {
        tilTitle.error = null
        tilAmount.error = null
        tilDate.error = null
        tilDescription.error = null
        textCategoryError.visibility = View.GONE
        textTransactionTypeError.visibility = View.GONE
        textPaymentMethodError.visibility = View.GONE
    }

    private fun parseAmount(raw: String): Double? {
        val cleaned = raw.replace("$", "").replace(",", "").trim()
        return cleaned.toDoubleOrNull()
    }

    private fun transactionTypeFromSpinner(position: Int): String? {
        return when (position) {
            1 -> TransactionType.INCOME
            2 -> TransactionType.EXPENSE
            else -> null
        }
    }

    private fun paymentMethodFromSpinner(position: Int): String? {
        return when (position) {
            1 -> PaymentMethod.CASH
            2 -> PaymentMethod.DEBIT_CARD
            3 -> PaymentMethod.CREDIT_CARD
            4 -> PaymentMethod.BANK_TRANSFER
            5 -> PaymentMethod.OTHER
            else -> null
        }
    }

    private fun transactionTypeSpinnerIndex(transactionType: String): Int {
        return when (transactionType) {
            TransactionType.INCOME -> 1
            TransactionType.EXPENSE -> 2
            else -> 0
        }
    }

    private fun paymentMethodSpinnerIndex(paymentMethod: String): Int {
        return when (paymentMethod) {
            PaymentMethod.CASH -> 1
            PaymentMethod.DEBIT_CARD -> 2
            PaymentMethod.CREDIT_CARD -> 3
            PaymentMethod.BANK_TRANSFER -> 4
            PaymentMethod.OTHER -> 5
            else -> 0
        }
    }

    companion object {
        const val EXTRA_EDIT_MODE = "extra_edit_mode"
        const val EXTRA_EXPENSE_ID = "extra_expense_id"

        fun createIntent(context: Context): Intent =
            Intent(context, ExpenseFormActivity::class.java).apply {
                putExtra(EXTRA_EDIT_MODE, false)
            }

        fun editIntent(context: Context, expenseId: Int): Intent =
            Intent(context, ExpenseFormActivity::class.java).apply {
                putExtra(EXTRA_EDIT_MODE, true)
                putExtra(EXTRA_EXPENSE_ID, expenseId)
            }
    }
}
