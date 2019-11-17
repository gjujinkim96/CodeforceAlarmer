package com.example.codeforcealarmer

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

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

        fun formatTime(seconds: Long?) : String{
            if (seconds == null){
                return "Unknown"
            }

            val instant = Instant.ofEpochSecond(seconds)
            val curZoneId = ZoneId.systemDefault()
            val zoneDateTime = ZonedDateTime.ofInstant(instant, curZoneId)

            val formatStyle = FormatStyle.valueOf("SHORT")
            var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(formatStyle)
            dateTimeFormatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HH:mm'UTC'x", Locale.getDefault())
            return dateTimeFormatter.format(zoneDateTime)
            return zoneDateTime.toString()
        }
    }
}