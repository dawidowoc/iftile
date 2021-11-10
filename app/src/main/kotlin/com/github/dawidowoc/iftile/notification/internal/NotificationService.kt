package com.github.dawidowoc.iftile.notification.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.dawidowoc.iftile.R

class NotificationService(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val NOTIFICATION_CHANNEL_ID = "com.github.dawidowoc.iftile.notification.channel.id"
    private val NOTIFICATION_ID = 1

    init {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    /**
     * Sends a notification. If a previously sent notification has not been dismissed, updates it.
     */
    fun sendNotification(title: String, text: String) {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification);
        }
    }
}