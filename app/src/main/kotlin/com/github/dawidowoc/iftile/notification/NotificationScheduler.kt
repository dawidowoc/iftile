package com.github.dawidowoc.iftile.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Date

data class Notification(val title: String, val text: String)

private val NOTIFICATION_TITLE_INTENT_EXTRA_KEY = "NOTIFICATION_TITLE"
private val NOTIFICATION_TEXT_INTENT_EXTRA_KEY = "NOTIFICATION_TEXT"

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        NotificationService(context!!).sendNotification(
            intent!!.getStringExtra(NOTIFICATION_TITLE_INTENT_EXTRA_KEY)!!,
            intent.getStringExtra(NOTIFICATION_TEXT_INTENT_EXTRA_KEY)!!
        )
    }
}

class NotificationScheduler(private val context: Context) {
    private val pendingIntentsList = mutableListOf<PendingIntent>()
    private var nextRequestCode = 0

    @Synchronized
    fun scheduleDailyNotification(notification: Notification, startDate: Date) {
        if (nextRequestCode == Int.MAX_VALUE) {
            throw IllegalStateException(
                "Too much scheduled daily notifications - max " + Int.MAX_VALUE
            )
        }

        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.putExtra(NOTIFICATION_TITLE_INTENT_EXTRA_KEY, notification.title)
        intent.putExtra(NOTIFICATION_TEXT_INTENT_EXTRA_KEY, notification.text)

        val pendingIntent = PendingIntent.getBroadcast(context, nextRequestCode++, intent, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startDate.toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )

        pendingIntentsList.add(pendingIntent)
    }

    @Synchronized
    fun cancelAll() = pendingIntentsList.forEach { intent ->
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(intent)
        nextRequestCode = 0
    }
}
