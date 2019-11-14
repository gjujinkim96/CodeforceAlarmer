package com.example.codeforcealarmer

import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import java.lang.StringBuilder
import java.text.DecimalFormat

class FormatHelper {
    companion object{
        fun formatSeconds(seconds: Long) : String{
            var duration = Duration.ofSeconds(seconds)
            val day = duration.toDays()
            duration = duration.minusDays(day)
            val hour = duration.toHours()
            duration = duration.minusHours(hour)
            val min = duration.toMinutes()


            val ar : ArrayList<Long> = arrayListOf()
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