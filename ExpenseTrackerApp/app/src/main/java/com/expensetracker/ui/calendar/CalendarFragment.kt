package com.expensetracker.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.expensetracker.ui.base.BaseFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.expensetracker.databinding.FragmentCalendarBinding
import com.expensetracker.utils.DateUtils
import java.text.NumberFormat
import java.util.Locale

class CalendarFragment : BaseFragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarAdapter: CalendarDayAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // Track locally so we can drive prev/next without touching the ViewModel until confirmed
    private var currentYear  = DateUtils.getCurrentYear()
    private var currentMonth = DateUtils.getCurrentMonth()  // already 1-based in their DateUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarAdapter = CalendarDayAdapter(currentYear, currentMonth) { /* day click */ }
        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = calendarAdapter

        updateHeader()

        // ── Month navigation ──────────────────────────────────────────────────
        binding.btnPrevMonth.setOnClickListener {
            if (currentMonth == 1) { currentMonth = 12; currentYear-- }
            else currentMonth--
            updateCalendar()
        }

        binding.btnNextMonth.setOnClickListener {
            if (currentMonth == 12) { currentMonth = 1; currentYear++ }
            else currentMonth++
            updateCalendar()
        }

        // ── Daily totals from ViewModel ───────────────────────────────────────
        transactionViewModel.dailyTotals.observe(viewLifecycleOwner) { totals ->
            // DailyTotal.day comes from strftime('%d',...) so it's a String like "05"
            // Convert to Int for the adapter's map
            val map = totals.associate { (it.day.trimStart('0').toIntOrNull() ?: 1) to it.total }
            calendarAdapter.setDailyTotals(map)
        }
    }

    private fun updateCalendar() {
        // Tell ViewModel to switch month so dailyTotals LiveData re-emits
        transactionViewModel.setYearMonth(currentYear, currentMonth)
        // Rebuild the adapter for the new month grid
        calendarAdapter = CalendarDayAdapter(currentYear, currentMonth) { /* day click */ }
        binding.rvCalendar.adapter = calendarAdapter
        updateHeader()
    }

    private fun updateHeader() {
        binding.tvMonthYear.text = DateUtils.formatFullMonth(currentYear, currentMonth)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}