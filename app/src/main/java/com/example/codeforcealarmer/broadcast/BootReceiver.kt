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

class BootReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Received BOOT_COMPLETED", Toast.LENGTH_LONG).show()
        //Toast.makeText(context, "context $context  intent $intent", Toast.LENGTH_LONG).show()

        if (context == null){
            Log.v("BOOT_ALERT", "context is null")
            return
        }

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED){
            Log.v("BOOT_ALERT", "action is boot completed")
        }

        Log.v("BOOT_ALERT", "Boot received")
        val serviceIntent = Intent(context, AlarmSetService::class.java)
        Log.v("BOOT_ALERT", "made service intent")
        AlarmSetService.enqueueWork(context, serviceIntent)
        Log.v("BOOT_ALERT", "started service")
    }
}