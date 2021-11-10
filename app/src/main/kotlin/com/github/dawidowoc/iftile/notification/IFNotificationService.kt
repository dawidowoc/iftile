package com.github.dawidowoc.iftile.notification

import android.content.Context
import com.github.dawidowoc.iftile.R
import com.github.dawidowoc.iftile.model.IntermittentFastingTimeConfig


class IFNotificationService(
    private val notificationScheduler: NotificationScheduler,
    private val context: Context
) {
    private val eatingNotificationId = 0
    private val fastingNotificationId = 1

    fun rescheduleAll(fastingTimeConfig: IntermittentFastingTimeConfig) {
        notificationScheduler.scheduleDailyNotification(
            getEatingStartedNotification(context),
            fastingTimeConfig.fastingEndTime.hour,
            fastingTimeConfig.fastingEndTime.minute,
            eatingNotificationId
        )
        notificationScheduler.scheduleDailyNotification(
            getFastingStartedNotification(context),
            fastingTimeConfig.fastingStartTime.hour,
            fastingTimeConfig.fastingStartTime.minute,
            fastingNotificationId
        )
    }
}

private fun getEatingStartedNotification(context: Context): Notification {
    return Notification(
        context.resources.getString(R.string.eating_started_notification_title),
        context.resources.getString(R.string.eating_started_notification_text)
    )
}

private fun getFastingStartedNotification(context: Context): Notification {
    return Notification(
        context.resources.getString(R.string.fasting_started_notification_title),
        context.resources.getString(R.string.fasting_started_notification_text)
    )
}