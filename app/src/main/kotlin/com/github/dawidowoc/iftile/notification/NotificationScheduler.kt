package com.github.dawidowoc.iftile.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

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
    fun scheduleDailyNotification(notification: Notification, hour: Int, minute: Int) {
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
            getNextOccuranceTimestampInMillis(hour, minute),
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )

        pendingIntentsList.add(pendingIntent)
    }

    private fun getNextOccuranceTimestampInMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    @Synchronized
    fun cancelAll() = pendingIntentsList.forEach { intent ->
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(intent)
        nextRequestCode = 0
    }
}
