package com.example.codeforcealarmer.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import com.example.codeforcealarmer.format.FormatHelper
import kotlinx.coroutines.runBlocking

class DataSaverService : JobIntentService() {
    companion object{
        val JOB_ID = 10
        fun enqueueWork(context: Context, work: Intent){
            enqueueWork(context, DataSaverService::class.java, JOB_ID, work)
        }
    }

    val alarmOffsetRepo by lazy{
        (application as MyApplication).appContainer.alarmOffsetRepo
    }

    val contestRepo by lazy{
        (application as MyApplication).appContainer.contestRepo
    }

    override fun onHandleWork(intent: Intent) {
        val key = getString(R.string.intent_alarm_data)
        val bytes = intent?.extras?.getByteArray(key) ?: throw IllegalArgumentException()
        val alarmData = ParcelConverter.unmarshall(bytes, AlarmOffsetWithStartTime.CREATOR)
        Log.v("SERVICE_TEST", "got service $alarmData")

        var contestTitle: String = ""
        runBlocking {
            contestTitle = contestRepo.getName(alarmData.id)
        }

        val channelId = getString(R.string.alarm_notifyer_channel_id)
        var builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$contestTitle is starting in 1 Hour!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationId = alarmData.id
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }

        runBlocking {
            Log.v("ALARM_INPUT", "delete from service")
            alarmOffsetRepo.delete(alarmData.id)
        }
    }
}