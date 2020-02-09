package service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.codeforcealarmer.NotificationMaker
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import kotlinx.coroutines.runBlocking

class HandleIncomingAlarmService : JobIntentService() {
    private val alarmOffsetRepo by lazy {
        (application as MyApplication).appContainer.alarmOffsetRepo
    }

    private val contestRepo by lazy{
        (application as MyApplication).appContainer.contestRepo
    }

    companion object{
            const val JOB_ID = 10
            fun enqueueWork(context: Context, intent: Intent) {
                enqueueWork(context, HandleIncomingAlarmService::class.java, JOB_ID, intent)
            }
    }

    override fun onHandleWork(intent: Intent) {
        val key = getString(R.string.intent_alarm_data)
        val bytes = intent.extras.getByteArray(key) ?: throw IllegalArgumentException()
        val alarmData = ParcelConverter.unmarshall(bytes, AlarmOffsetWithStartTime.creator)

        val contestTitle = runBlocking {
            contestRepo.getName(alarmData.id)
        }

        NotificationMaker.addNotification(this, contestTitle, alarmData)

        runBlocking {
            alarmOffsetRepo.delete(alarmData.id, alarmData.data)
        }
    }
}