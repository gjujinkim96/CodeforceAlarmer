package com.example.codeforcealarmer.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.AppContainer

class MyApplication : Application() {
    lateinit var appContainer: AppContainer

    companion object {
        lateinit var application: MyApplication
            private set
    }

    override fun onCreate() {
        Log.v("BOOT_ALERT", "MyApplication onCreate")
        super.onCreate()
        application = this
        appContainer = AppContainer(this)
        Log.v("BOOT_ALERT", "MyApplication made appContainer")

        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        Log.v("BOOT_ALERT", "MyApplication createNotificationChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.contest_alarm_name)
            val descText = getString(R.string.contest_alarm_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.alarm_notifyer_channel_id), name, importance)
            channel.description = descText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}