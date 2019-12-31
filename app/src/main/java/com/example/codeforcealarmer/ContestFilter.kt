package com.example.codeforcealarmer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime

data class ContestFilter(var divFilter: ContestType = ContestType.makeAllTrue(), var startTime: LocalTime = LocalTime.now(),
                         var endTime: LocalTime = LocalTime.now()) {
    fun copyWithNull(newStartTime: LocalTime? = null,
             newEndTime: LocalTime? = null,
             newContestType: ContestType? = null) : ContestFilter{
        val copied = this.copy()
        if (newStartTime != null)
            copied.startTime = newStartTime
        if (newEndTime != null)
            copied.endTime = newEndTime
        if (newContestType != null)
            copied.divFilter = newContestType

        return copied
    }
}