package com.github.dawidowoc.iftile.tile

import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileProviderService
import androidx.wear.tiles.TimelineBuilders
import com.github.dawidowoc.iftile.R
import com.github.dawidowoc.iftile.model.IntermittentFastingService
import com.github.dawidowoc.iftile.model.IntermittentFastingTimeConfig
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.time.Clock
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId

private const val RESOURCES_VERSION = "1"
private val REFRESH_INTERVAL = Duration.ofMinutes(1)

class IfTileService : TileProviderService() {
    private val intermittentFastingService =
        IntermittentFastingService(
            Clock.systemDefaultZone(), ZoneId.systemDefault(),
            IntermittentFastingTimeConfig(LocalTime.of(20, 0), LocalTime.of(16, 0))
        )

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest):
            ListenableFuture<TileBuilders.Tile> {
        return Futures.immediateFuture(
            TileBuilders.Tile.builder().setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(REFRESH_INTERVAL.toMillis())
                .setTimeline(
                    TimelineBuilders.Timeline.builder().addTimelineEntry(
                        TimelineBuilders.TimelineEntry.builder().setLayout(
                            LayoutElementBuilders.Layout.builder().setRoot(
                                LayoutElementBuilders.Text.builder().setText(getStatus())
                            )
                        )
                    )
                ).build()
        )
    }

    private fun getStatus() = if (intermittentFastingService.isFasting())
        resources.getString(R.string.fasting_status_label) else
        resources.getString(R.string.eating_status_label)

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest):
            ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.builder().setVersion(
                RESOURCES_VERSION
            ).build()
        )
    }
}