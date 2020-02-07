package com.example.codeforcealarmer.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.codeforcealarmer.NotificationMaker
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import kotlinx.coroutines.runBlocking

class DataSaverService : JobIntentService() {
    companion object{
        private const val JOB_ID = 10
        const val GROUP_ID = "DATA_GROUP_ID"
        const val SUMMARY_ID = -1

        fun enqueueWork(context: Context, work: Intent){
            enqueueWork(context, DataSaverService::class.java, JOB_ID, work)
        }
    }

    private val alarmOffsetRepo by lazy{
        (application as MyApplication).appContainer.alarmOffsetRepo
    }

    private val contestRepo by lazy{
        (application as MyApplication).appContainer.contestRepo
    }

    override fun onHandleWork(intent: Intent) {
        val key = getString(R.string.intent_alarm_data)
        val bytes = intent.extras?.getByteArray(key) ?: throw IllegalArgumentException()
        val alarmData = ParcelConverter.unmarshall(bytes, AlarmOffsetWithStartTime.creator)
        Log.v("SERVICE_TEST", "got service $alarmData")

        var contestTitle = ""
        runBlocking {
            contestTitle = contestRepo.getName(alarmData.id)
        }

        NotificationMaker.addNotification(this, contestTitle, alarmData)

        runBlocking {
            Log.v("ALARM_INPUT", "delete from service")
            alarmOffsetRepo.delete(alarmData.id, alarmData.data)
        }
    }
}