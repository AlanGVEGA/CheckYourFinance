package com.example.checkyourfinance

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var adapter: ExpenseAdapter

    private val allExpenses: List<ExpenseUiModel> = listOf(
        ExpenseUiModel(1, "Starbucks", "Food & Dining", 8.90, "Today", CAT_FOOD, isFavorite = true),
        ExpenseUiModel(2, "Amazon", "Shopping", 120.50, "Today", CAT_SHOPPING),
        ExpenseUiModel(3, "Uber", "Transport", 15.40, "Yesterday", CAT_TRANSPORT),
        ExpenseUiModel(4, "Netflix", "Entertainment", 15.99, "May 8", CAT_ENTERTAINMENT),
        ExpenseUiModel(5, "Electricity Bill", "Bills & Utilities", 95.00, "May 6", CAT_BILLS),
        ExpenseUiModel(6, "Target", "Shopping", 42.30, "May 4", CAT_SHOPPING),
        ExpenseUiModel(7, "McDonald's", "Food & Dining", 12.75, "May 3", CAT_FOOD)
    )

    private var selectedCategory: String = CAT_ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.expense_list_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindHeader()
        bindFab()
        setupRecycler()
        bindChips()

        setSelectedChip(CAT_ALL)
        renderList()
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
            Toast.makeText(this, R.string.toast_create_expense_coming_soon, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.button_empty_add).setOnClickListener {
            Toast.makeText(this, R.string.toast_create_expense_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.recycler_expenses)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ExpenseAdapter { _ ->
            Toast.makeText(this, R.string.toast_expense_detail_coming_soon, Toast.LENGTH_SHORT).show()
        }
        recycler.adapter = adapter
    }

    private fun bindChips() {
        findViewById<View>(R.id.chip_all).setOnClickListener { onChipClicked(CAT_ALL) }
        findViewById<View>(R.id.chip_food).setOnClickListener { onChipClicked(CAT_FOOD) }
        findViewById<View>(R.id.chip_transport).setOnClickListener { onChipClicked(CAT_TRANSPORT) }
        findViewById<View>(R.id.chip_shopping).setOnClickListener { onChipClicked(CAT_SHOPPING) }
        findViewById<View>(R.id.chip_bills).setOnClickListener { onChipClicked(CAT_BILLS) }
        findViewById<View>(R.id.chip_entertainment).setOnClickListener { onChipClicked(CAT_ENTERTAINMENT) }
    }

    private fun onChipClicked(category: String) {
        setSelectedChip(category)
        renderList()
    }

    private fun setSelectedChip(category: String) {
        selectedCategory = category

        setChipSelected(R.id.chip_all, category == CAT_ALL)
        setChipSelected(R.id.chip_food, category == CAT_FOOD)
        setChipSelected(R.id.chip_transport, category == CAT_TRANSPORT)
        setChipSelected(R.id.chip_shopping, category == CAT_SHOPPING)
        setChipSelected(R.id.chip_bills, category == CAT_BILLS)
        setChipSelected(R.id.chip_entertainment, category == CAT_ENTERTAINMENT)
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
        val filtered = if (selectedCategory == CAT_ALL) {
            allExpenses
        } else {
            allExpenses.filter { it.categoryType == selectedCategory }
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

    companion object {
        const val CAT_ALL = "ALL"
        const val CAT_FOOD = "FOOD"
        const val CAT_TRANSPORT = "TRANSPORT"
        const val CAT_SHOPPING = "SHOPPING"
        const val CAT_BILLS = "BILLS"
        const val CAT_ENTERTAINMENT = "ENTERTAINMENT"
    }
}

