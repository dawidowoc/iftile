package com.github.dawidowoc.iftile.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.dawidowoc.iftile.notification.internal.NotificationService
import java.util.Calendar
import java.util.Date

data class Notification(val title: String, val text: String)

private val NOTIFICATION_TITLE_INTENT_EXTRA_KEY =
    "com.github.dawidowoc.iftile.notification.notification.title"
private val NOTIFICATION_TEXT_INTENT_EXTRA_KEY =
    "com.github.dawidowoc.iftile.notification.notification.text"

class NotificationBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "NotificationBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        NotificationService(context!!).sendNotification(
            intent?.getStringExtra(NOTIFICATION_TITLE_INTENT_EXTRA_KEY)!!,
            intent.getStringExtra(NOTIFICATION_TEXT_INTENT_EXTRA_KEY)!!
        )
        Log.d(TAG, "Notification sent")
    }
}

class NotificationScheduler(private val context: Context) {
    private val TAG = "NotificationScheduler"

    fun scheduleDailyNotification(
        notification: Notification,
        hour: Int,
        minute: Int,
        notificationId: Int
    ) {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.putExtra(NOTIFICATION_TITLE_INTENT_EXTRA_KEY, notification.title)
        intent.putExtra(NOTIFICATION_TEXT_INTENT_EXTRA_KEY, notification.text)

        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextOccurrence = getNextOccuranceTimestampInMillis(hour, minute)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            nextOccurrence,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        val nextOccurrenceDate = Date(nextOccurrence)
        Log.d(
            TAG,
            "Scheduled notification with ID $notificationId to run, starting on $nextOccurrenceDate"
        )
    }

    private fun getNextOccuranceTimestampInMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        return if (calendar >= Calendar.getInstance()) calendar.timeInMillis else {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            return calendar.timeInMillis
        }
    }
}
