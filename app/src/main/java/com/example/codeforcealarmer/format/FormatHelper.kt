package com.example.codeforcealarmer.format

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class FormatHelper {
    companion object{
        // TODO : Change this function name to more appropriate
        fun formatSeconds(seconds: Long?) : String{
            if (seconds == null)
                return "Unknown"

            var sec = seconds
            var isNegative = false

            if (sec < 0) {
                sec *= -1
                isNegative = true
            }

            var duration = Duration.ofSeconds(sec)
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
            return "${ if (isNegative) "-" else ""}" +  ar.joinToString(":", transform = { decimalFormat.format(it) })
        }

        fun formatTime(seconds: Long?) : String{
            if (seconds == null){
                return "Unknown"
            }

            val instant = Instant.ofEpochSecond(seconds)
            val curZoneId = ZoneId.systemDefault()
            val zoneDateTime = ZonedDateTime.ofInstant(instant, curZoneId)

            val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy\nHH:mm", Locale.getDefault())
            return dateTimeFormatter.format(zoneDateTime)
        }
    }
}