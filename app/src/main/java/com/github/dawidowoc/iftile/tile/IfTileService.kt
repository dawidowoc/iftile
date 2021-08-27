package com.github.dawidowoc.iftile.tile

import androidx.wear.tiles.*
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.time.Duration
import java.util.Date

private const val RESOURCES_VERSION = "1"
private val REFRESH_INTERVAL = Duration.ofMinutes(1)

class IfTileService : TileProviderService() {
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest):
            ListenableFuture<TileBuilders.Tile> {
        return Futures.immediateFuture(
            TileBuilders.Tile.builder().setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(REFRESH_INTERVAL.toMillis())
                .setTimeline(
                    TimelineBuilders.Timeline.builder().addTimelineEntry(
                        TimelineBuilders.TimelineEntry.builder().setLayout(
                            LayoutElementBuilders.Layout.builder().setRoot(
                                LayoutElementBuilders.Text.builder().setText(Date().toString())
                            )
                        )
                    )
                ).build()
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