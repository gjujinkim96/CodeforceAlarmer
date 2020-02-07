package com.example.codeforcealarmer.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.codeforcealarmer.R
import com.example.codeforcealarmer.application.MyApplication
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.datalayer.dataholder.ParcelConverter
import com.example.codeforcealarmer.ui.activity_fragments.MainActivity
import kotlinx.coroutines.runBlocking

class DataSaverService : JobIntentService() {
    companion object{
        val JOB_ID = 10
        val GROUP_ID = "DATA_GROUP_ID"
        val SUMMARY_ID = -1

        fun enqueueWork(context: Context, work: Intent){
            enqueueWork(context, DataSaverService::class.java, JOB_ID, work)
        }
    }

    val alarmOffsetRepo by lazy{
        (application as MyApplication).appContainer.alarmOffsetRepo
    }

    val contestRepo by lazy{
        (application as MyApplication).appContainer.contestRepo
    }

    override fun onHandleWork(intent: Intent) {
        val key = getString(R.string.intent_alarm_data)
        val bytes = intent?.extras?.getByteArray(key) ?: throw IllegalArgumentException()
        val alarmData = ParcelConverter.unmarshall(bytes, AlarmOffsetWithStartTime.creator)
        Log.v("SERVICE_TEST", "got service $alarmData")

        var contestTitle: String = ""
        runBlocking {
            contestTitle = contestRepo.getName(alarmData.id)
        }

        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0)

        val channelId = getString(R.string.alarm_notifyer_channel_id)
        val notificationId = alarmData.id

        var notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentTitle("Starting in ${alarmData.data.name}")
            .setContentText(contestTitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(GROUP_ID)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .build()

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = nm.activeNotifications

        Log.v("NOTIFICATION_CHECK", "${activeNotifications.size}")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && activeNotifications.size == 0 || (activeNotifications.size == 1 && activeNotifications[0].id == notificationId)){
            with(NotificationManagerCompat.from(this)){
                notify(notificationId, notification)
            }

            runBlocking {
                Log.v("ALARM_INPUT", "delete from service")
                alarmOffsetRepo.delete(alarmData.id, alarmData.data)
            }
        }else{
            val inboxStyle = NotificationCompat.InboxStyle()
            var currentAdded = false
            activeNotifications.forEach {
                if (it.id == SUMMARY_ID){
                    val extra_lines = it.notification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
                    extra_lines.forEach {
                        if (it.endsWith(contestTitle)){
                            val spannable = SpannableString("Starting in ${alarmData.data.name} $contestTitle")
                            spannable.setSpan(ForegroundColorSpan(getColor(R.color.firstPartSummaryColor)),
                                0,
                                "Starting in ${alarmData.data.name}".length,
                                Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                            inboxStyle.addLine(spannable)
                            currentAdded = true
                        }else{
                            inboxStyle.addLine(it)
                        }
                    }
                    return@forEach
                }

                if (it.id == notificationId){
                    val spannable = SpannableString("Starting in ${alarmData.data.name} $contestTitle")
                    spannable.setSpan(ForegroundColorSpan(getColor(R.color.firstPartSummaryColor)),
                        0,
                        "Starting in ${alarmData.data.name}".length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    inboxStyle.addLine(spannable)
                    currentAdded = true
                }else{
                    val extras = it.notification.extras
                    val spannable = SpannableString("${extras.get(Notification.EXTRA_TITLE)} ${extras.get(Notification.EXTRA_TEXT)}")
                    spannable.setSpan(ForegroundColorSpan(getColor(R.color.firstPartSummaryColor)),
                        0,
                        "${extras.get(Notification.EXTRA_TITLE)}".length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    inboxStyle.addLine(spannable)
                }
            }

            if (!currentAdded){
                val spannable = SpannableString("Starting in ${alarmData.data.name} $contestTitle")
                spannable.setSpan(ForegroundColorSpan(getColor(R.color.firstPartSummaryColor)),
                    0,
                    "Starting in ${alarmData.data.name}".length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                inboxStyle.addLine(spannable)
            }

            inboxStyle.setBigContentTitle("CodeforceAlarmer")

            var summaryNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_ID)
                .setGroupSummary(true)
                .build()


            with(NotificationManagerCompat.from(this)){
                notify(notificationId, notification)
                notify(SUMMARY_ID, summaryNotification)
            }

            runBlocking {
                Log.v("ALARM_INPUT", "delete from service")
                alarmOffsetRepo.delete(alarmData.id, alarmData.data)
            }
        }
    }
}