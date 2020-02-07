package com.example.codeforcealarmer.broadcast

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.getSystemService
import com.example.codeforcealarmer.service.AlarmSetService

class AlarmUpdateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null){
            Log.v("ALARM_UPDATE",
                "AlarmUpdateReceiver:onReceive:intent action:null")
        }else {
            Log.v(
                "ALARM_UPDATE",
                "AlarmUpdateReceiver:onReceive:intent action:${intent?.action}"
            )
        }

        if (context == null)
            return

        Log.v("BOOT_ALERT", "Boot received")
        val serviceIntent = Intent(context, AlarmSetService::class.java)
        Log.v("BOOT_ALERT", "made service intent")
        AlarmSetService.enqueueWork(context, serviceIntent)
        Log.v("BOOT_ALERT", "started service")
    }
}