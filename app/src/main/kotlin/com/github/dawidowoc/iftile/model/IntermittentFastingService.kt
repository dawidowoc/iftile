package com.github.dawidowoc.iftile.model

import java.time.Clock
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId

class IntermittentFastingService(
    private val clock: Clock,
    private val zoneId: ZoneId,
    private val fastingTimeConfig: IntermittentFastingTimeConfig
) {

    fun isFasting(): Boolean {
        val currentLocalTime = getCurrentLocalTime()

        return if (isFastingSpanningTwoDays()) {
            !(currentLocalTime > fastingTimeConfig.fastingEndTime
                    && currentLocalTime < fastingTimeConfig.fastingStartTime)
        } else {
            currentLocalTime > fastingTimeConfig.fastingStartTime
                    && currentLocalTime < fastingTimeConfig.fastingEndTime
        }
    }

    fun timeLeftToStatusChange(): Duration {
        val duration = if (isFasting()) Duration.between(
            getCurrentLocalTime(),
            fastingTimeConfig.fastingEndTime
        ) else Duration.between(getCurrentLocalTime(), fastingTimeConfig.fastingStartTime)

        return if (duration.isNegative) Duration.ofDays(1).minus(duration.abs()) else duration
    }

    private fun getCurrentLocalTime() = LocalTime.from(clock.instant().atZone(zoneId))

    private fun isFastingSpanningTwoDays() =
        fastingTimeConfig.fastingStartTime > fastingTimeConfig.fastingEndTime
}
