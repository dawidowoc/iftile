package com.github.dawidowoc.iftile.persistence

import android.content.SharedPreferences
import com.github.dawidowoc.iftile.model.IntermittentFastingTimeConfig
import java.time.LocalTime

const val FASTING_START_H_KEY = "FASTING_START_H"
const val FASTING_START_MIN_KEY = "FASTING_START_MIN"
const val FASTING_STOP_H_KEY = "FASTING_STOP_H"
const val FASTING_STOP_MIN_KEY = "FASTING_STOP_MIN"

const val FASTING_START_H_DEFAULT = 20
const val FASTING_START_MIN_DEFAULT = 0
const val FASTING_STOP_H_DEFAULT = 12
const val FASTING_STOP_MIN_DEFAULT = 0

class FastingTimeConfigDao(private val sharedPreferences: SharedPreferences) {
    fun setConfig(fastingTimeConfig: IntermittentFastingTimeConfig) {
        sharedPreferences.edit()
            .putInt(FASTING_START_H_KEY, fastingTimeConfig.fastingStartTime.hour)
            .putInt(FASTING_START_MIN_KEY, fastingTimeConfig.fastingStartTime.minute)
            .putInt(FASTING_STOP_H_KEY, fastingTimeConfig.fastingEndTime.hour)
            .putInt(FASTING_STOP_MIN_KEY, fastingTimeConfig.fastingEndTime.minute)
            .apply()
    }

    fun getConfig(): IntermittentFastingTimeConfig {
        return IntermittentFastingTimeConfig(
            LocalTime.of(
                sharedPreferences.getInt(FASTING_START_H_KEY, FASTING_START_H_DEFAULT),
                sharedPreferences.getInt(FASTING_START_MIN_KEY, FASTING_START_MIN_DEFAULT)
            ),
            LocalTime.of(
                sharedPreferences.getInt(FASTING_STOP_H_KEY, FASTING_STOP_H_DEFAULT),
                sharedPreferences.getInt(FASTING_STOP_MIN_KEY, FASTING_STOP_MIN_DEFAULT)
            )
        )
    }
}