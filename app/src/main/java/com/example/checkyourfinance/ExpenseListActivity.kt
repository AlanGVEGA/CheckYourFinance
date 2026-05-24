package com.example.checkyourfinance

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var adapter: ExpenseAdapter
    private lateinit var textTotalSpentAmount: TextView

    private var backingList: List<ExpenseUiModel> = emptyList()

    private var selectedCategory: String = ExpenseCategories.ALL

    private val app: CheckYourFinanceApplication
        get() = application as CheckYourFinanceApplication

    private val expenseFormLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            reloadFromPersistence()
        }
    }

    private val expenseDetailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            reloadFromPersistence()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.expense_list_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textTotalSpentAmount = findViewById(R.id.text_total_spent_amount)

        bindHeader()
        bindFab()
        setupRecycler()
        bindChips()

        setSelectedChip(ExpenseCategories.ALL)
    }

    override fun onResume() {
        super.onResume()
        reloadFromPersistence()
    }

    private fun reloadFromPersistence() {
        lifecycleScope.launch {
            val session = app.sessionManager
            val list = withContext(Dispatchers.IO) {
                if (session.isLoggedIn()) {
                    app.repository.getExpensesForUser(session.getUserId())
                } else {
                    ExpenseSampleData.expenses
                }
            }
            backingList = list
            val total = list.sumOf { signedAmount(it) }
            textTotalSpentAmount.text = formatCurrencyAmount(total)
            renderList()
        }
    }

    private fun bindHeader() {
        findViewById<View>(R.id.button_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.button_search).setOnClickListener {
            Toast.makeText(this, R.string.toast_search_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindFab() {
        findViewById<FloatingActionButton>(R.id.fab_add_expense).setOnClickListener {
            expenseFormLauncher.launch(ExpenseFormActivity.createIntent(this))
        }

        findViewById<View>(R.id.button_empty_add).setOnClickListener {
            expenseFormLauncher.launch(ExpenseFormActivity.createIntent(this))
        }
    }

    private fun setupRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.recycler_expenses)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ExpenseAdapter(
            onItemClick = { expense ->
                expenseDetailLauncher.launch(ExpenseDetailActivity.createIntent(this, expense.id))
            },
            onEditClick = { expense ->
                expenseFormLauncher.launch(ExpenseFormActivity.editIntent(this, expense.id))
            },
            onItemLongClick = { expense ->
                expenseFormLauncher.launch(ExpenseFormActivity.editIntent(this, expense.id))
            }
        )
        recycler.adapter = adapter
    }

    private fun bindChips() {
        findViewById<View>(R.id.chip_all).setOnClickListener { onChipClicked(ExpenseCategories.ALL) }
        findViewById<View>(R.id.chip_food).setOnClickListener { onChipClicked(ExpenseCategories.FOOD) }
        findViewById<View>(R.id.chip_transport).setOnClickListener { onChipClicked(ExpenseCategories.TRANSPORT) }
        findViewById<View>(R.id.chip_shopping).setOnClickListener { onChipClicked(ExpenseCategories.SHOPPING) }
        findViewById<View>(R.id.chip_bills).setOnClickListener { onChipClicked(ExpenseCategories.BILLS) }
        findViewById<View>(R.id.chip_entertainment).setOnClickListener {
            onChipClicked(ExpenseCategories.ENTERTAINMENT)
        }
    }

    private fun onChipClicked(category: String) {
        setSelectedChip(category)
        renderList()
    }

    private fun setSelectedChip(category: String) {
        selectedCategory = category

        setChipSelected(R.id.chip_all, category == ExpenseCategories.ALL)
        setChipSelected(R.id.chip_food, category == ExpenseCategories.FOOD)
        setChipSelected(R.id.chip_transport, category == ExpenseCategories.TRANSPORT)
        setChipSelected(R.id.chip_shopping, category == ExpenseCategories.SHOPPING)
        setChipSelected(R.id.chip_bills, category == ExpenseCategories.BILLS)
        setChipSelected(R.id.chip_entertainment, category == ExpenseCategories.ENTERTAINMENT)
    }

    private fun setChipSelected(chipId: Int, selected: Boolean) {
        val chip = findViewById<TextView>(chipId)
        chip.isSelected = selected
        chip.setBackgroundResource(
            if (selected) R.drawable.chip_selected_background else R.drawable.chip_unselected_background
        )
        chip.setTextColor(
            getColor(if (selected) R.color.primary_dark_green else R.color.text_main)
        )
    }

    private fun renderList() {
        val filtered = if (selectedCategory == ExpenseCategories.ALL) {
            backingList
        } else {
            backingList.filter { it.categoryType == selectedCategory }
        }

        adapter.submitList(filtered)
        updateEmptyState(filtered.isEmpty())
        updateSummaryCount(filtered.size)
    }

    private fun updateSummaryCount(count: Int) {
        findViewById<TextView>(R.id.text_total_spent_count).text =
            resources.getQuantityString(R.plurals.expense_transactions_count, count, count)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        findViewById<View>(R.id.empty_state_container).visibility = if (isEmpty) View.VISIBLE else View.GONE
        findViewById<View>(R.id.recycler_expenses).visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun signedAmount(expense: ExpenseUiModel): Double {
        return if (expense.transactionType == TransactionType.INCOME) {
            expense.amount
        } else {
            -expense.amount
        }
    }
}
