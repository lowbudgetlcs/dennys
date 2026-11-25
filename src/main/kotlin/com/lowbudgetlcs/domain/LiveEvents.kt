package com.lowbudgetlcs.domain

import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.models.Series
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class LiveEvent(
    val name: String,
    val data: JsonObject,
)

/**
 * Change notifications for the live stream. Events carry ids only, so clients refetch through the normal GETs.
 * Emitting never blocks or fails a write: with no subscriber the event is dropped, and a slow one loses its oldest.
 */
class LiveEvents {
    private val flow =
        MutableSharedFlow<LiveEvent>(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events: SharedFlow<LiveEvent> = flow

    fun seriesUpdated(series: Series) {
        flow.tryEmit(
            LiveEvent(
                "series.updated",
                buildJsonObject {
                    put("seriesId", series.id.value)
                    put("eventId", series.eventId.value)
                    put("status", series.status.name)
                },
            ),
        )
    }

    fun gameStatsReady(game: Game) {
        flow.tryEmit(
            LiveEvent(
                "game.stats_ready",
                buildJsonObject {
                    put("gameId", game.id.value)
                    put("seriesId", game.seriesId.value)
                },
            ),
        )
    }
}
