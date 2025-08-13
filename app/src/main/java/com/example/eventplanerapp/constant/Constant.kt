package com.example.eventplanerapp.constant

object Constant {
    fun startOfDayMillis(timeMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = timeMillis }
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun endOfDayMillis(startOfDayMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = startOfDayMillis }
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.add(java.util.Calendar.MILLISECOND, -1)
        return cal.timeInMillis
    }
}