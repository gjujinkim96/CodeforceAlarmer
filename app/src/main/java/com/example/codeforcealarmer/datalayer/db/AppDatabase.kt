package com.example.codeforcealarmer.datalayer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetDao
import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetWithStartTimeDao
import com.example.codeforcealarmer.datalayer.dao.ContestDao
import com.example.codeforcealarmer.datalayer.dao.ContestWithAlarmDao
import com.example.codeforcealarmer.datalayer.dataholder.*

@Database(entities = [Contest::class, AlarmOffset::class], version = 1)
@TypeConverters(PhaseConverters::class, AlarmDataConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contestDao() : ContestDao
    abstract fun alarmOffsetDao() : AlarmOffsetDao
    abstract fun alarmOffsetWithStartTimeDao() : AlarmOffsetWithStartTimeDao
    abstract fun contestWithAlarmDao() : ContestWithAlarmDao
}