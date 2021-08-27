package com.github.dawidowoc.iftile.tile

import androidx.wear.tiles.*
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

private const val RESOURCES_VERSION = "1"

class IfTileService : TileProviderService() {
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest):
            ListenableFuture<TileBuilders.Tile> {
        return Futures.immediateFuture(
            TileBuilders.Tile.builder().setResourcesVersion(RESOURCES_VERSION).setTimeline(
                TimelineBuilders.Timeline.builder().addTimelineEntry(
                    TimelineBuilders.TimelineEntry.builder().setLayout(
                        LayoutElementBuilders.Layout.builder().setRoot(
                            LayoutElementBuilders.Text.builder().setText("Hello!")
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