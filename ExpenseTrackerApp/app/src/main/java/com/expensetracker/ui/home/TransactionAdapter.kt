package com.expensetracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.data.db.entities.Transaction
import com.expensetracker.data.db.entities.TransactionCategory
import com.expensetracker.data.db.entities.TransactionType
import com.expensetracker.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.TextView

class TransactionAdapter(
    private val onDelete: (Transaction) -> Unit
) : ListAdapter<TransactionListItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionListItem.Header -> VIEW_TYPE_HEADER
            is TransactionListItem.Item -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.expensetracker.R.layout.item_transaction_header, parent, false)
            HeaderViewHolder(view as TextView)
        } else {
            val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TransactionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TransactionListItem.Header -> (holder as HeaderViewHolder).bind(item.dateString)
            is TransactionListItem.Item -> (holder as TransactionViewHolder).bind(item.transaction)
        }
    }

    inner class HeaderViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(dateString: String) {
            textView.text = dateString
        }
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            val category = try {
                TransactionCategory.valueOf(transaction.category)
            } catch (e: Exception) {
                TransactionCategory.OTHER
            }

            binding.tvCategoryEmoji.text = category.emoji
            binding.tvCategoryName.text = category.displayName
            binding.tvNote.text = transaction.note.ifBlank { "No note" }
            binding.tvDate.text = dateFormat.format(Date(transaction.date))

            val isExpense = transaction.type == TransactionType.EXPENSE.name
            binding.tvAmount.text = if (isExpense) {
                "-${currencyFormat.format(transaction.amount)}"
            } else {
                "+${currencyFormat.format(transaction.amount)}"
            }

            val context = binding.root.context
            binding.tvAmount.setTextColor(
                if (isExpense) context.getColor(com.expensetracker.R.color.expense_red)
                else context.getColor(com.expensetracker.R.color.income_green)
            )

            binding.root.setOnLongClickListener {
                onDelete(transaction)
                true
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionListItem>() {
        override fun areItemsTheSame(oldItem: TransactionListItem, newItem: TransactionListItem): Boolean {
            return if (oldItem is TransactionListItem.Header && newItem is TransactionListItem.Header) {
                oldItem.dateString == newItem.dateString
            } else if (oldItem is TransactionListItem.Item && newItem is TransactionListItem.Item) {
                oldItem.transaction.id == newItem.transaction.id
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: TransactionListItem, newItem: TransactionListItem): Boolean {
            return oldItem == newItem
        }
    }
}
