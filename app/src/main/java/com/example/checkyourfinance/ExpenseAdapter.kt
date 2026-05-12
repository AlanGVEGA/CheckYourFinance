package com.example.checkyourfinance

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val onItemClick: (ExpenseUiModel) -> Unit,
    private val onEditClick: (ExpenseUiModel) -> Unit,
    private val onItemLongClick: (ExpenseUiModel) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val items: MutableList<ExpenseUiModel> = mutableListOf()

    fun submitList(newItems: List<ExpenseUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view, onItemClick, onEditClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ExpenseViewHolder(
        itemView: View,
        private val onClick: (ExpenseUiModel) -> Unit,
        private val onEditClick: (ExpenseUiModel) -> Unit,
        private val onLongClick: (ExpenseUiModel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.text_expense_title)
        private val category: TextView = itemView.findViewById(R.id.text_expense_category)
        private val date: TextView = itemView.findViewById(R.id.text_expense_date)
        private val amount: TextView = itemView.findViewById(R.id.text_expense_amount)
        private val indicator: View = itemView.findViewById(R.id.view_category_indicator)
        private val favorite: TextView = itemView.findViewById(R.id.text_expense_favorite)
        private val editButton: ImageButton = itemView.findViewById(R.id.button_edit_expense)

        fun bind(model: ExpenseUiModel) {
            title.text = model.title
            category.text = model.category
            date.text = model.date
            amount.text = itemView.context.getString(R.string.expense_amount_format, model.amount)
            favorite.visibility = if (model.isFavorite) View.VISIBLE else View.GONE

            val indicatorColorRes = when (model.categoryType) {
                ExpenseCategories.FOOD -> R.color.category_food
                ExpenseCategories.TRANSPORT -> R.color.category_transport
                ExpenseCategories.SHOPPING -> R.color.category_shopping
                ExpenseCategories.BILLS -> R.color.category_bills
                ExpenseCategories.ENTERTAINMENT -> R.color.category_entertainment
                ExpenseCategories.OTHER -> R.color.border_soft
                else -> R.color.border_soft
            }
            (indicator.background as? GradientDrawable)?.setColor(
                ContextCompat.getColor(itemView.context, indicatorColorRes)
            )

            itemView.setOnClickListener { onClick(model) }
            itemView.setOnLongClickListener {
                onLongClick(model)
                true
            }
            editButton.setOnClickListener { onEditClick(model) }
        }
    }
}
