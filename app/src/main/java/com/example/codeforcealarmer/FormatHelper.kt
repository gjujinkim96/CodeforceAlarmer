package com.example.codeforcealarmer

import java.text.DecimalFormat

class FormatHelper {
    companion object{
        const val SECONDS_PER_HOUR = 3600
        const val SECONDS_PER_MIN = 60
        fun formatSeconds(seconds: Int) : String{
            val hour = seconds / SECONDS_PER_HOUR
            val min = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MIN
            val decimalFormat = DecimalFormat("00")
            return "${decimalFormat.format(hour)}:${decimalFormat.format(min)}"
        }
    }
}