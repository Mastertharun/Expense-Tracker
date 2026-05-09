package com.expensetracker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val monthYearFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    private val fullMonthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun getYearMonth(date: Long = System.currentTimeMillis()): String =
        monthYearFormat.format(Date(date))

    fun getCurrentMonthYear(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${String.format("%02d", cal.get(Calendar.MONTH) + 1)}"
    }

    fun getYearMonth(year: Int, month: Int): String =
        "$year-${String.format("%02d", month)}"

    fun formatDate(date: Long): String = displayFormat.format(Date(date))

    fun formatTime(date: Long): String = timeFormat.format(Date(date))

    fun formatFullMonth(year: Int, month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return fullMonthFormat.format(cal.time)
    }

    fun getStartOfDay(date: Long): Long {
        val cal = Calendar.getInstance()
        cal.time = Date(date)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfDay(date: Long): Long {
        val cal = Calendar.getInstance()
        cal.time = Date(date)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return (cal.get(Calendar.DAY_OF_WEEK) - 1)
    }
}
