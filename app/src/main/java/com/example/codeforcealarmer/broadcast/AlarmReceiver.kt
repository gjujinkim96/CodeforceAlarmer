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
import service.HandleIncomingAlarmService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.v("SERVICE_TEST", "AlarmReceiver:onReceive")
        HandleIncomingAlarmService.enqueueWork(context, intent)
    }
}
