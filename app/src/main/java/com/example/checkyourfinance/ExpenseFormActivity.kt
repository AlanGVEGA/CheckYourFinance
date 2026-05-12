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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ExpenseFormActivity : AppCompatActivity() {

    private lateinit var textScreenTitle: TextView
    private lateinit var textScreenSubtitle: TextView
    private lateinit var tilTitle: TextInputLayout
    private lateinit var inputTitle: TextInputEditText
    private lateinit var tilAmount: TextInputLayout
    private lateinit var inputAmount: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var textCategoryError: TextView
    private lateinit var tilDate: TextInputLayout
    private lateinit var inputDate: TextInputEditText
    private lateinit var tilDescription: TextInputLayout
    private lateinit var inputDescription: TextInputEditText
    private lateinit var buttonSave: MaterialButton

    private var isEditMode: Boolean = false

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
        val expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)

        setupCategorySpinner()
        applyModeUi()

        if (isEditMode && expenseId >= 0) {
            val expense = ExpenseSampleData.findById(expenseId)
            if (expense != null) {
                populateFromExpense(expense)
            }
        }

        findViewById<View>(R.id.button_back).setOnClickListener { finish() }

        buttonSave.setOnClickListener {
            if (validateAndSubmit()) {
                val message = if (isEditMode) {
                    R.string.toast_expense_updated_success
                } else {
                    R.string.toast_expense_created_success
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
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
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    textCategoryError.visibility = View.GONE
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

        val labels = resources.getStringArray(R.array.expense_form_categories)
        val index = labels.indexOf(expense.category).takeIf { it >= 0 } ?: 0
        if (index > 0) {
            spinnerCategory.setSelection(index)
        }
    }

    private fun formatAmountForInput(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format("%.2f", amount)
        }
    }

    private fun validateAndSubmit(): Boolean {
        clearFieldErrors()

        val title = inputTitle.text?.toString()?.trim().orEmpty()
        val amountRaw = inputAmount.text?.toString()?.trim().orEmpty()
        val date = inputDate.text?.toString()?.trim().orEmpty()
        val categoryIndex = spinnerCategory.selectedItemPosition

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
    }

    private fun parseAmount(raw: String): Double? {
        val cleaned = raw.replace("$", "").replace(",", "").trim()
        return cleaned.toDoubleOrNull()
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
