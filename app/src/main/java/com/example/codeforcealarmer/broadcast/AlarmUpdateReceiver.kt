package com.example.codeforcealarmer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.codeforcealarmer.ContestAlarmManger
import com.example.codeforcealarmer.application.MyApplication
import service.UpdateAlarmService

class AlarmUpdateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        Log.v("SERVICE_TEST", "AlarmUpdateReceiver:onReceive")
        intent?.let{ UpdateAlarmService.enqueueWork(context, it) }
    }
}