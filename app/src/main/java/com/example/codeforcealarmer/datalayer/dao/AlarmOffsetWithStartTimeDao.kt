package com.example.codeforcealarmer.datalayer.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime

@Dao
interface AlarmOffsetWithStartTimeDao {
    @Query("SELECT Contest.id, Contest.startTimeSeconds, AlarmOffset.data  FROM Contest, AlarmOffset WHERE Contest.id = AlarmOffset.id")
    fun getAlarmedData() : List<AlarmOffsetWithStartTime>
}