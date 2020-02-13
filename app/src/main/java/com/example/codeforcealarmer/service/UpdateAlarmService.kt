package com.example.codeforcealarmer.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.example.codeforcealarmer.ContestAlarmManger
import com.example.codeforcealarmer.application.MyApplication
import kotlinx.coroutines.runBlocking

class UpdateAlarmService : JobIntentService() {
    private val alarmWithStartTimeRepo by lazy {
        (application as MyApplication).appContainer.alarmOffsetWithStartTimeRepo
    }

    companion object{
        const val JOB_ID = 11

        fun enqueueWork(context: Context, intent: Intent){
            enqueueWork(context, UpdateAlarmService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val alarmData = runBlocking { alarmWithStartTimeRepo.getAlarmedData() }
        ContestAlarmManger.setContestAlarm(this@UpdateAlarmService, *alarmData.toTypedArray())
    }
}