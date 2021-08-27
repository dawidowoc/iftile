package com.github.dawidowoc.iftile.model

import java.time.Clock
import java.time.LocalTime
import java.time.ZoneId

class IntermittentFastingService(
    private val clock: Clock,
    private val zoneId: ZoneId,
    private val fastingTimeConfig: IntermittentFastingTimeConfig
) {

    fun isFasting(): Boolean {
        val currentLocalTime = LocalTime.from(clock.instant().atZone(zoneId))

        return if (isFastingSpanningTwoDays()) {
            !(currentLocalTime > fastingTimeConfig.fastingEndTime
                    && currentLocalTime < fastingTimeConfig.fastingStartTime)
        } else {
            currentLocalTime > fastingTimeConfig.fastingStartTime
                    && currentLocalTime < fastingTimeConfig.fastingEndTime
        }
    }

    private fun isFastingSpanningTwoDays() =
        fastingTimeConfig.fastingStartTime > fastingTimeConfig.fastingEndTime
}
