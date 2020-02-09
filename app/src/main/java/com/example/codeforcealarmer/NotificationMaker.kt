package com.example.codeforcealarmer

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.codeforcealarmer.datalayer.dataholder.AlarmOffsetWithStartTime
import com.example.codeforcealarmer.ui.activity_fragments.MainActivity

class NotificationMaker {
    companion object {
        const val GROUP_ID = "DATA_GROUP_ID"
        const val SUMMARY_ID = -1

        fun addNotification(context: Context, contestTitle: String, alarmData: AlarmOffsetWithStartTime) {
            val channelId = context.getString(R.string.alarm_notifyer_channel_id)
            val notificationId = alarmData.id

            val pendingIntent = Intent(context, MainActivity::class.java).let {
                PendingIntent.getActivity(context, 0, it, 0)
            }

            // make normal notification
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle(makeFrontTitle(alarmData))
                .setContentText(contestTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_ID)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .build()

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val activeNotifications = nm.activeNotifications

            // for android version lower than N summary should be shown when there is 2 or more notification
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && activeNotifications.isEmpty()|| (activeNotifications.size == 1 && activeNotifications[0].id == notificationId)){
                with(NotificationManagerCompat.from(context)){
                    notify(notificationId, notification)
                }
            }else {
                // making group summary
                // only applies to android version lower than N
                val inboxStyle = NotificationCompat.InboxStyle()
                var currentAdded = false
                activeNotifications.forEach {activeNotification ->
                    // if activeNotification is summary notification
                    if (activeNotification.id == SUMMARY_ID){
                        // added lines in summary notification
                        val extraLines = activeNotification.notification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
                        extraLines.forEach {
                            if (it.endsWith(contestTitle)){
                                inboxStyle.addLine(makeNotificationText(context, alarmData, contestTitle))
                                currentAdded = true
                            }else{
                                inboxStyle.addLine(it)
                            }
                        }
                    }else if (activeNotification.id == notificationId){ // if notification is begin updated
                        inboxStyle.addLine(makeNotificationText(context, alarmData, contestTitle))
                        currentAdded = true
                    }else{ // normal notification
                        val extras = activeNotification.notification.extras
                        val frontPart = extras.get(Notification.EXTRA_TITLE) as String? ?: throw IllegalArgumentException()
                        val oldContestTitle = extras.get(Notification.EXTRA_TEXT) as String? ?: throw IllegalArgumentException()
                        inboxStyle.addLine(makeNotificationText(context, frontPart, oldContestTitle))
                    }
                }

                if (!currentAdded){
                    inboxStyle.addLine(makeNotificationText(context, alarmData, contestTitle))
                }

                inboxStyle.setBigContentTitle("CodeforceAlarmer")

                // make summary notification
                val summaryNotification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_delete)
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true)
                    .build()


                with(NotificationManagerCompat.from(context)){
                    notify(notificationId, notification)
                    notify(SUMMARY_ID, summaryNotification)
                }
            }
        }

        fun makeFrontTitle(alarmData: AlarmOffsetWithStartTime): String =
            "Starting in ${alarmData.data.name}"

        fun makeNotificationText(context: Context, alarmData: AlarmOffsetWithStartTime, contestTitle: String): SpannableString{
            val textFrontOfTitle = makeFrontTitle(alarmData)
            return makeNotificationText(context, textFrontOfTitle, contestTitle)
        }


        fun makeNotificationText(context: Context, textFrontOfTitle: String, contestTitle: String): SpannableString =
            SpannableString("$textFrontOfTitle $contestTitle").apply {
                setSpan(
                    ForegroundColorSpan(context.getColor(R.color.firstPartSummaryColor)),
                    0,
                    textFrontOfTitle.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }

    }
}
