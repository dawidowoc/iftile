package com.github.dawidowoc.iftile.model

import java.time.LocalTime

data class IntermittentFastingTimeConfig(
    val fastingStartTime: LocalTime,
    val fastingEndTime: LocalTime
)
