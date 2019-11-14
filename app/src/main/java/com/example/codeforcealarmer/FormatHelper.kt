package com.example.codeforcealarmer

import java.lang.StringBuilder
import java.text.DecimalFormat

class FormatHelper {
    companion object{
        const val MIN_PER_SEC = 60
        const val HOUR_PER_MIN = 60
        const val DAY_PER_HOUR = 24
        fun formatSeconds(seconds: Int) : String{
            var min = seconds / MIN_PER_SEC
            var hour = min / HOUR_PER_MIN
            var day = hour / DAY_PER_HOUR
            hour %= DAY_PER_HOUR
            min %= HOUR_PER_MIN

            val ar : ArrayList<Int> = arrayListOf()
            if (day > 0)
                ar.add(day)
            ar.apply {
                add(hour)
                add(min)
            }

            val decimalFormat = DecimalFormat("00")
            return ar.joinToString(":", transform = { decimalFormat.format(it) })
        }
    }
}