package com.expensetracker.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.expensetracker.ui.base.BaseFragment
import com.expensetracker.data.db.entities.TransactionCategory
import com.expensetracker.databinding.FragmentStatisticsBinding
import com.expensetracker.utils.DateUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import java.text.NumberFormat
import java.util.Locale

class StatisticsFragment : BaseFragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    private val chartColors = listOf(
        Color.parseColor("#6C63FF"), Color.parseColor("#FF6584"),
        Color.parseColor("#43E97B"), Color.parseColor("#F7971E"),
        Color.parseColor("#4FC3F7"), Color.parseColor("#CE93D8"),
        Color.parseColor("#F48FB1"), Color.parseColor("#A5D6A7"),
        Color.parseColor("#FFD54F")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBarChart()
        setupPieChart()
        updateHeader()

        // ── Month navigation ──────────────────────────────────────────────────
        binding.btnPrevMonth.setOnClickListener {
            val (year, month) = currentYearMonth()
            if (month == 1) transactionViewModel.setYearMonth(year - 1, 12)
            else            transactionViewModel.setYearMonth(year, month - 1)
            updateHeader()
        }

        binding.btnNextMonth.setOnClickListener {
            val (year, month) = currentYearMonth()
            if (month == 12) transactionViewModel.setYearMonth(year + 1, 1)
            else             transactionViewModel.setYearMonth(year, month + 1)
            updateHeader()
        }

        // ── Bar chart: daily spending ─────────────────────────────────────────
        transactionViewModel.dailyTotals.observe(viewLifecycleOwner) { totals ->
            val entries = totals
                .sortedBy { it.day.trimStart('0').toIntOrNull() ?: 1 }
                .map { BarEntry(
                    it.day.trimStart('0').toFloatOrNull() ?: 1f,
                    it.total.toFloat()
                ) }

            val dataset = BarDataSet(entries, "Daily Spending").apply {
                color = Color.parseColor("#6C63FF")
                valueTextColor = Color.WHITE
                valueTextSize = 10f
            }
            binding.barChart.data = BarData(dataset)
            binding.barChart.invalidate()
        }

        // ── Pie chart: category breakdown ─────────────────────────────────────
        transactionViewModel.categoryBreakdown.observe(viewLifecycleOwner) { cats ->
            val entries = cats.map { cs ->
                PieEntry(
                    cs.total.toFloat(),
                    try { TransactionCategory.valueOf(cs.category).emoji }
                    catch (e: Exception) { cs.category }
                )
            }
            val dataset = PieDataSet(entries, "").apply {
                colors = chartColors.take(entries.size)
                valueTextColor = Color.WHITE
                valueTextSize = 12f
                sliceSpace = 3f
            }
            binding.pieChart.data = PieData(dataset)
            binding.pieChart.invalidate()

            if (cats.isNotEmpty()) {
                val top = cats.first()
                val name = try { TransactionCategory.valueOf(top.category).displayName }
                catch (e: Exception) { top.category }
                binding.tvTopCategory.text = "Top: $name — ${currencyFormat.format(top.total)}"
            }

            binding.tvTotalExpense.text =
                "Total: ${currencyFormat.format(cats.sumOf { it.total })}"
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns (year, 1-based month) from the ViewModel's current selectedYearMonth */
    private fun currentYearMonth(): Pair<Int, Int> {
        val parts = transactionViewModel.selectedYearMonth.value.split("-")
        return parts[0].toInt() to parts[1].toInt()
    }

    private fun updateHeader() {
        val (year, month) = currentYearMonth()
        binding.tvMonthYear.text = DateUtils.formatFullMonth(year, month)
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            legend.isEnabled = false
            setFitBars(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.WHITE
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
            setBackgroundColor(Color.TRANSPARENT)
            animateY(600)
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.TRANSPARENT)
            legend.isEnabled = true
            legend.textColor = Color.WHITE
            setBackgroundColor(Color.TRANSPARENT)
            animateY(800)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}