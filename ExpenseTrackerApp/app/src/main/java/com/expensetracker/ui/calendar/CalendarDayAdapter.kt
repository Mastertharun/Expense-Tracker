package com.expensetracker.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.R
import com.expensetracker.databinding.ItemCalendarDayBinding
import com.expensetracker.utils.DateUtils
import java.text.NumberFormat
import java.util.Locale

class CalendarDayAdapter(
    private val year: Int,
    private val month: Int,
    private val onDayClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val daysInMonth = DateUtils.getDaysInMonth(year, month)
    private val firstDayOffset = DateUtils.getFirstDayOfWeek(year, month)
    private val totalCells = firstDayOffset + daysInMonth
    private var dailyTotals: Map<Int, Double> = emptyMap()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // 0 = empty cell, positive = actual day number
    private val cells: List<Int> = buildList {
        repeat(firstDayOffset) { add(0) }
        for (d in 1..daysInMonth) add(d)
    }

    fun setDailyTotals(totals: Map<Int, Double>) {
        dailyTotals = totals
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (cells[position] == 0) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_calendar_empty, parent, false)
            object : RecyclerView.ViewHolder(view) {}
        } else {
            val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DayViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val day = cells[position]
        if (day > 0 && holder is DayViewHolder) {
            holder.bind(day)
        }
    }

    override fun getItemCount() = cells.size

    inner class DayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Int) {
            binding.tvDay.text = day.toString()
            val spending = dailyTotals[day] ?: 0.0
            val context = binding.root.context

            if (spending > 0) {
                binding.tvSpending.text = "₹${spending.toInt()}"
                val color = when {
                    spending > 1000 -> context.getColor(R.color.expense_red)
                    spending > 500 -> context.getColor(R.color.expense_orange)
                    else -> context.getColor(R.color.accent_green)
                }
                binding.dayIndicator.setBackgroundColor(color)
                binding.dayIndicator.alpha = 0.3f
            } else {
                binding.tvSpending.text = ""
                binding.dayIndicator.setBackgroundColor(0x00000000)
            }

            binding.root.setOnClickListener { onDayClick(day) }
        }
    }
}
