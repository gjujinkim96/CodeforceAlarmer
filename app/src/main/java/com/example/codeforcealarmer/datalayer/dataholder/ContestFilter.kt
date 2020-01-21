package com.example.codeforcealarmer.datalayer.dataholder

import android.util.Log
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

data class ContestFilter(var divFilter: ContestType = ContestType.makeAllTrue(), var startTime: LocalTime = LocalTime.now(),
                         var endTime: LocalTime = LocalTime.now()) {
    fun copy() = ContestFilter(this.divFilter.copy(), this.startTime, this.endTime)

    // for newContestType it copied
    fun copyWithNull(newStartTime: LocalTime? = null,
             newEndTime: LocalTime? = null,
             newDiv1: Boolean? = null,
             newDiv2: Boolean? = null,
             newDiv3: Boolean? = null,
             newOther: Boolean? = null) : ContestFilter {
        val copied = this.copy()
        if (newStartTime != null)
            copied.startTime = newStartTime
        if (newEndTime != null)
            copied.endTime = newEndTime
        if (newDiv1 != null)
            copied.divFilter.div1 = newDiv1
        if (newDiv2 != null)
            copied.divFilter.div2 = newDiv2
        if (newDiv3 != null)
            copied.divFilter.div3 = newDiv3
        if (newOther != null)
            copied.divFilter.other = newOther
        Log.v("FILTER_DEBUG", "contestfilter: copy: \ncurrent: $this \n copied: $copied")
        return copied
    }

    fun contains(contest: ContestWithAlarm) : Boolean {
        if (!divFilter.contains(contest.contestType))
            return false

        val contestStartTime = contest.startTimeSeconds
        if (contestStartTime != null) {
            val instant = Instant.ofEpochSecond(contestStartTime)
            val zoneId = ZoneId.systemDefault()
            val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
            val localTime = zonedDateTime.toLocalTime()
            if (startTime <= endTime && startTime <= localTime && localTime <= endTime)
                return true
            else if (startTime <= localTime || localTime <= endTime)
                return true

            return false
        }else
            return false
    }
}