package com.example.checkyourfinance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.GradientDrawable

class ExpenseAdapter(
    private val onItemClick: (ExpenseUiModel) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val items: MutableList<ExpenseUiModel> = mutableListOf()

    fun submitList(newItems: List<ExpenseUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ExpenseViewHolder(
        itemView: View,
        private val onClick: (ExpenseUiModel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.text_expense_title)
        private val category: TextView = itemView.findViewById(R.id.text_expense_category)
        private val date: TextView = itemView.findViewById(R.id.text_expense_date)
        private val amount: TextView = itemView.findViewById(R.id.text_expense_amount)
        private val indicator: View = itemView.findViewById(R.id.view_category_indicator)
        private val favorite: TextView = itemView.findViewById(R.id.text_expense_favorite)

        fun bind(model: ExpenseUiModel) {
            title.text = model.title
            category.text = model.category
            date.text = model.date
            amount.text = itemView.context.getString(R.string.expense_amount_format, model.amount)
            favorite.visibility = if (model.isFavorite) View.VISIBLE else View.GONE

            val indicatorColorRes = when (model.categoryType) {
                ExpenseListActivity.CAT_FOOD -> R.color.category_food
                ExpenseListActivity.CAT_TRANSPORT -> R.color.category_transport
                ExpenseListActivity.CAT_SHOPPING -> R.color.category_shopping
                ExpenseListActivity.CAT_BILLS -> R.color.category_bills
                ExpenseListActivity.CAT_ENTERTAINMENT -> R.color.category_entertainment
                else -> R.color.border_soft
            }
            (indicator.background as? GradientDrawable)?.setColor(
                ContextCompat.getColor(itemView.context, indicatorColorRes)
            )

            itemView.setOnClickListener { onClick(model) }
        }
    }
}

