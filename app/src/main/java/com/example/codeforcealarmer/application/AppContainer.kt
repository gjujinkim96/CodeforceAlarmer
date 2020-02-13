package com.example.codeforcealarmer.application

import android.content.Context
import android.webkit.DownloadListener
import androidx.room.Room
import com.example.codeforcealarmer.datalayer.dao.AlarmOffsetDao
import com.example.codeforcealarmer.datalayer.dao.ContestDao
import com.example.codeforcealarmer.datalayer.db.AppDatabase
import com.example.codeforcealarmer.datalayer.repo.*
import com.example.codeforcealarmer.network.DownloadEstimator
import com.example.codeforcealarmer.network.NetworkChecker

class AppContainer(val context: Context) {

    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "my_database").build()
    val contestRepo = ContestRepo(context, db.contestDao())
    val alarmOffsetRepo =
        AlarmOffsetRepo(db.alarmOffsetDao())
    val alarmOffsetWithStartTimeRepo =
        AlarmOffsetWithStartTimeRepo(
            db.alarmOffsetWithStartTimeDao()
        )
    val contestFilterRepo =
        ContestFilterRepo(context)

    val contestWithAlarmRepo =  ContestWithAlarmRepo(db.contestDao(), db.alarmOffsetDao())
}