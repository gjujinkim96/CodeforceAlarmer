package com.example.codeforcealarmer

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AlarmOffsetWithStartTimeDao {
    @Query("SELECT Contest.id, Contest.startTimeSeconds, AlarmOffset.`offset`  FROM Contest, AlarmOffset WHERE Contest.id = AlarmOffset.id")
    suspend fun getAlarmedData() : List<AlarmOffsetWithStartTime>
}