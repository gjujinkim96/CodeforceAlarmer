package com.example.codeforcealarmer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.codeforcealarmer.broadcast.AlarmReceiver
import com.example.codeforcealarmer.datalayer.dataholder.AlarmData
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffset
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter

class ContestAlarmManger {
    companion object{
        fun setContestAlarm(context: Context, vararg alarmSetData: AlarmOffsetWithStartTime){
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (ele in alarmSetData){
                if (ele.startTimeSeconds == null)
                    continue

                val intent = Intent(context, AlarmReceiver::class.java).apply{
                    putExtra(context.getString(R.string.intent_alarm_data), ParcelConverter.marshall(ele))
                }


                val pendingIntent = ele.run { AlarmOffset(id, data) }
                    .let{ PendingIntent.getBroadcast(context, it.hashCode(), intent, 0)}


                with(ele){
                    val tmpStartTime = startTimeSeconds ?: return@with

                    if (data != AlarmData.ZERO){
                        am.set(AlarmManager.RTC, tmpStartTime * 1000 - AlarmData.getOffsetInMilli(data), pendingIntent)
                    }else{
                        am.setExact(AlarmManager.RTC, tmpStartTime * 1000, pendingIntent)
                    }
                }
            }
        }

        fun cancelContestAlarm(context: Context, alarmData: AlarmOffsetWithStartTime){
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply{
                putExtra(context.getString(R.string.intent_alarm_data), ParcelConverter.marshall(alarmData))
            }

            val pendingIntent = alarmData.run { AlarmOffset(id, data) }.let {
                PendingIntent.getBroadcast(context, it.hashCode(), intent, 0)
            }

            am.cancel(pendingIntent)
        }
    }
}
