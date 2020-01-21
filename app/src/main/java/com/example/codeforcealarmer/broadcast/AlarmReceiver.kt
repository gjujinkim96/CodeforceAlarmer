package com.example.codeforcealarmer.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import com.example.codeforcealarmer.datalayer.repo.AlarmOffsetRepo
import com.example.codeforcealarmer.service.DataSaverService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.v("BOOT_ALERT", "alarm receiver received")
        val key = context.getString(R.string.intent_alarm_data)
        val bytes = intent.extras.getByteArray(key) ?: throw IllegalArgumentException()
        val alarmData = ParcelConverter.unmarshall(bytes, AlarmOffsetWithStartTime.CREATOR)
        Log.v("SERVICE_TEST", "got alarm $alarmData")


        Log.v("ALARM_SET", "get at ${System.currentTimeMillis()}")
        // service section
        val serviceIntent = Intent(context, DataSaverService::class.java).apply {
            putExtras(intent)
        }

        DataSaverService.enqueueWork(context, serviceIntent)
    }
}