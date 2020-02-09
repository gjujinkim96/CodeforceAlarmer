package com.example.codeforcealarmer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.codeforcealarmer.NotificationMaker
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmOffsetRepo by lazy {
            (context.applicationContext as MyApplication).appContainer.alarmOffsetRepo
        }

        val contestRepo by lazy{
            (context.applicationContext as MyApplication).appContainer.contestRepo
        }

        Log.v("BOOT_ALERT", "alarm receiver received")
        val key = context.getString(R.string.intent_alarm_data)
        val bytes = intent.extras.getByteArray(key) ?: throw IllegalArgumentException()
        val alarmData = ParcelConverter.unmarshall(bytes, AlarmOffsetWithStartTime.creator)
        Log.v("SERVICE_TEST", "got alarm $alarmData")

        val contestTitle = contestRepo.getName(alarmData.id)


        NotificationMaker.addNotification(context, contestTitle, alarmData)

        alarmOffsetRepo.delete(alarmData.id, alarmData.data)
    }
}
