package com.github.dawidowoc.iftile.tile

import android.preference.PreferenceManager
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.LayoutElementBuilders.Row
import androidx.wear.tiles.LayoutElementBuilders.Text
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileProviderService
import androidx.wear.tiles.TimelineBuilders
import com.github.dawidowoc.iftile.R
import com.github.dawidowoc.iftile.model.IntermittentFastingService
import com.github.dawidowoc.iftile.persistence.FastingTimeConfigDao
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.time.Clock
import java.time.Duration
import java.time.ZoneId
import kotlin.math.floor

private const val RESOURCES_VERSION = "1"
private val REFRESH_INTERVAL = Duration.ofMinutes(1)

class IfTileService : TileProviderService() {
    private lateinit var intermittentFastingService: IntermittentFastingService

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest):
            ListenableFuture<TileBuilders.Tile> {
        intermittentFastingService =
            IntermittentFastingService(
                Clock.systemDefaultZone(), ZoneId.systemDefault(),
                FastingTimeConfigDao(PreferenceManager.getDefaultSharedPreferences(this))
            )

        return Futures.immediateFuture(
            TileBuilders.Tile.builder().setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(REFRESH_INTERVAL.toMillis())
                .setTimeline(
                    TimelineBuilders.Timeline.builder().addTimelineEntry(
                        TimelineBuilders.TimelineEntry.builder().setLayout(
                            LayoutElementBuilders.Layout.builder().setRoot(
                                render()
                            )
                        )
                    )
                ).build()
        )
    }

    private fun render(): LayoutElement = Column.builder()
        .addContent(Row.builder().addContent(Text.builder().setText(renderStatus())))
        .addContent(
            Row.builder().addContent(Text.builder().setText(renderTimeLeftToStatusChange()))
        )
        .build()

    private fun renderStatus() = if (intermittentFastingService.isFasting())
        resources.getString(R.string.fasting_status_label) else
        resources.getString(R.string.eating_status_label)

    private fun renderTimeLeftToStatusChange(): String {
        val interval = intermittentFastingService.timeLeftToStatusChange()
        return String.format(
            "${resources.getString(R.string.status_change_time_left_label)} %dh %dmin",
            floor(interval.toMinutes() / 60.0).toInt(),
            interval.toMinutes() % 60
        )
    }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest):
            ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.builder().setVersion(
                RESOURCES_VERSION
            ).build()
        )
    }
}
