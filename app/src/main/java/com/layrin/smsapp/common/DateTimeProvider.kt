package com.layrin.smsapp.common

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.layrin.smsapp.R
import java.util.*

class DateTimeProvider(
    private val context: Context
) {

    private val yesterdayString = context.getString(R.string.yesterday)

    fun getTimeCondensed(time: Long): String {
        val msgTime = Calendar.getInstance().apply { timeInMillis = time }
        val now = Calendar.getInstance()

        return when {
            DateUtils.isToday(time) -> DateFormat.format(
                if (DateFormat.is24HourFormat(context)) "H:mm" else "h:mm aa",
                msgTime
            ).toString()

            DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS) -> yesterdayString

            now[Calendar.WEEK_OF_YEAR] == msgTime[Calendar.WEEK_OF_YEAR] &&
                    now[Calendar.YEAR] == msgTime[Calendar.YEAR] -> DateFormat.format(
                "EEEE", msgTime
                    ).toString()

            now[Calendar.YEAR] == msgTime[Calendar.YEAR] -> DateFormat.format(
                "d MMMM", msgTime
            ).toString()

            else -> DateFormat.format("dd/MM/yyyy", msgTime).toString()
        }
    }

    fun getTimeFull(time: Long): String {
        val msgTime = Calendar.getInstance().apply { timeInMillis = time }
        val now = Calendar.getInstance()

        val timeString = DateFormat.format(
            if (DateFormat.is24HourFormat(context)) "H:mm" else "h:mm aa",
            msgTime
        ).toString()

        return when {
            DateUtils.isToday(time) -> timeString
            DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS) -> "$timeString,\n$yesterdayString"
            now[Calendar.YEAR] == msgTime[Calendar.YEAR] ->
                "$timeString,\n${DateFormat.format("d MMMM", msgTime)}"
            else -> "$timeString,\n${DateFormat.format("dd/MM/yyyy", msgTime)}"
        }
    }
}