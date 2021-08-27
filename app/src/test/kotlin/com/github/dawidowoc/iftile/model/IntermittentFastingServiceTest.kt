package com.github.dawidowoc.iftile.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

class IntermittentFastingServiceTest {
    @Test
    fun shouldReturnFastingTrueWhenCurrentTimeIsInFastingPeriod() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("2018-08-22T10:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isTrue()
    }

    @Test
    fun shouldReturnFastingFalseWhenCurrentTimeIsNotInFastingPeriod_currentTimeIsBeforeFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("2018-08-22T07:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isFalse()
    }

    @Test
    fun shouldReturnFastingFalseWhenCurrentTimeIsNotInFastingPeriod_currentTimeIsAfterFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("2018-08-22T16:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isFalse()
    }

    @Test
    fun shouldReturnFastingTrueWhenCurrentTimeIsInFastingPeriod_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("2018-08-22T21:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isTrue()
    }

    @Test
    fun shouldReturnFastingTrueWhenCurrentTimeIsInFastingPeriodNextDay_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("2018-08-22T06:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isTrue()
    }

    @Test
    fun shouldReturnFastingFalseWhenCurrentTimeIsInNotInFastingPeriod_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("2018-08-22T19:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isFalse()
    }

    private fun prepareTestData(
        currentInstant: Instant?, fastingTimeConfig: IntermittentFastingTimeConfig
    ): IntermittentFastingService {
        val clock = Clock.fixed(currentInstant, ZoneOffset.UTC)
        return IntermittentFastingService(
            clock, ZoneId.of("UTC"),
            fastingTimeConfig
        )
    }
}