package com.github.dawidowoc.iftile.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

private const val DATE = "2018-08-22"

class IntermittentFastingServiceTest {

    @Test
    fun shouldReturnFastingTrueWhenCurrentTimeIsInFastingPeriod() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T10:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isTrue()
    }

    @Test
    fun shouldReturnFastingFalseWhenCurrentTimeIsNotInFastingPeriod_currentTimeIsBeforeFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T07:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isFalse()
    }

    @Test
    fun shouldReturnFastingFalseWhenCurrentTimeIsNotInFastingPeriod_currentTimeIsAfterFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T16:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isFalse()
    }

    @Test
    fun shouldReturnFastingTrueWhenCurrentTimeIsInFastingPeriod_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T21:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isTrue()
    }

    @Test
    fun shouldReturnFastingTrueWhenCurrentTimeIsInFastingPeriodNextDay_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T06:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isTrue()
    }

    @Test
    fun shouldReturnFastingFalseWhenCurrentTimeIsInNotInFastingPeriod_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T19:00:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(8, 0)
            )
        )

        assertThat(intermittentFastingService.isFasting()).isFalse()
    }

    @Test
    fun shouldReturnTimeLeftToStatusSwitchWhenCurrentlyEating_currentTimeIsBeforeFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T08:25:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(10, 0),
                LocalTime.of(20, 0)
            )
        )

        assertThat(intermittentFastingService.timeLeftToStatusChange())
            .isEqualTo(Duration.ofHours(1).plusMinutes(35))
    }

    @Test
    fun shouldReturnTimeLeftToStatusSwitchWhenCurrentlyEating_currentTimeIsAfterFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T23:25:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(10, 0),
                LocalTime.of(20, 0)
            )
        )

        assertThat(intermittentFastingService.timeLeftToStatusChange())
            .isEqualTo(Duration.ofHours(10).plusMinutes(35))
    }

    @Test
    fun shouldReturnTimeLeftToStatusSwitchWhenCurrentlyFasting() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T12:45:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(10, 0),
                LocalTime.of(20, 0)
            )
        )

        assertThat(intermittentFastingService.timeLeftToStatusChange())
            .isEqualTo(Duration.ofHours(7).plusMinutes(15))
    }

    @Test
    fun shouldReturnTimeLeftToStatusSwitchWhenCurrentlyEating_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T18:05:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(16, 0)
            )
        )

        assertThat(intermittentFastingService.timeLeftToStatusChange())
            .isEqualTo(Duration.ofHours(1).plusMinutes(55))
    }

    @Test
    fun shouldReturnTimeLeftToStatusSwitchWhenCurrentlyFasting_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T21:45:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(16, 0)
            )
        )

        assertThat(intermittentFastingService.timeLeftToStatusChange())
            .isEqualTo(Duration.ofHours(18).plusMinutes(15))
    }

    @Test
    fun shouldReturnTimeLeftToStatusSwitchWhenCurrentlyFastingNextDay_fastingPeriodSpanningTwoDays() {
        val intermittentFastingService = prepareTestData(
            Instant.parse("${DATE}T02:45:00Z"), IntermittentFastingTimeConfig(
                LocalTime.of(20, 0),
                LocalTime.of(16, 0)
            )
        )

        assertThat(intermittentFastingService.timeLeftToStatusChange())
            .isEqualTo(Duration.ofHours(13).plusMinutes(15))
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