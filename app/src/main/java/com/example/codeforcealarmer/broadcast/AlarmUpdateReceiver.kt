package com.example.codeforcealarmer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.codeforcealarmer.ContestAlarmManger
import com.example.codeforcealarmer.application.MyApplication

class AlarmUpdateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        val alarmWithStartTimeRepo by lazy {
            (context.applicationContext as MyApplication).appContainer.alarmOffsetWithStartTimeRepo
        }


        val alarmData = alarmWithStartTimeRepo.getAlarmedData()
        ContestAlarmManger.setContestAlarm(context, *alarmData.toTypedArray())
    }
}