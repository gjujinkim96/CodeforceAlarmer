package com.example.codeforcealarmer.datalayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import com.example.codeforcealarmer.datalayer.dataholder.Contest
import com.example.codeforcealarmer.datalayer.dataholder.ContestType
import com.example.codeforcealarmer.datalayer.dataholder.ContestWithAlarm
import com.example.codeforcealarmer.datalayer.dataholder.Phase

@Dao
interface ContestWithAlarmDao {
    @Query(
    """
        SELECT Contest.id, name, div1, div2, div3, other, phase, durationSeconds, startTimeSeconds, offsetTime
        From Contest LEFT JOIN AlarmOffset ON Contest.id = AlarmOffset.id 
        WHERE phase BETWEEN :startPhase and :endPhase ORDER BY 
        CASE WHEN :isAsc = 1 THEN startTimeSeconds END ASC, 
        CASE WHEN :isAsc = 0 THEN startTimeSeconds END DESC
    """
    )
    fun getBetweenPhases(startPhase: Phase, endPhase: Phase, isAsc: Boolean) : LiveData<List<ContestWithAlarm>>
}
