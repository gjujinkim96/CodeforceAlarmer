package com.example.codeforcealarmer.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.broadcast.AlarmReceiver
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext

class AlarmSetService : JobIntentService() {
    companion object{
        val JOB_ID = 11
        fun enqueueWork(context: Context, work: Intent){
            enqueueWork(context, AlarmSetService::class.java, JOB_ID, work)
        }
    }

    val alarmWithStartTimeRepo by lazy { (application as MyApplication).appContainer.alarmOffsetWithStartTimeRepo }

    override fun onHandleWork(intent: Intent) {
        val handler =  Handler(Looper.getMainLooper())
        handler.post{
            Toast.makeText(this@AlarmSetService, "Call from Service", Toast.LENGTH_LONG).show()
        }

        runBlocking {
            val alarmData = alarmWithStartTimeRepo.getAlarmedData()
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmData.forEach {
                val intent = Intent(this@AlarmSetService, AlarmReceiver::class.java)
                intent.putExtra(getString(R.string.intent_alarm_data), ParcelConverter.marshall(it))

                val alarmIntent = PendingIntent.getBroadcast(this@AlarmSetService, it.id, intent, PendingIntent.FLAG_CANCEL_CURRENT)

                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5 * 1000, alarmIntent)
            }
        }
    }
}