package routes

import com.lowbudgetlcs.api.routes.v1.stream.streamRoutesV1
import com.lowbudgetlcs.domain.LiveEvents
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.types.TeamId
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.sse.sse
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import io.ktor.client.plugins.sse.SSE as ClientSSE

// testApplication blocks its thread, and among concurrent specs on a small runner this endless stream can starve.
@Isolate
class StreamRouteTest :
    StringSpec({
        "the stream relays live events as named server-sent events" {
            val live = LiveEvents()
            val series =
                Series(
                    SeriesId(9),
                    EventId(4),
                    EventStage.PLAYOFFS,
                    3,
                    TeamId(1) to TeamId(2),
                    null,
                    SeriesStatus.COMPLETE,
                )
            testApplication {
                install(SSE)
                routing { streamRoutesV1(live) }
                val client = createClient { install(ClientSSE) }
                coroutineScope {
                    // A shared flow only reaches subscribers already listening, so keep emitting until one is.
                    val emitter =
                        launch {
                            while (isActive) {
                                live.seriesUpdated(series)
                                delay(20)
                            }
                        }
                    client.sse("/stream") {
                        val event = incoming.first { it.event == "series.updated" }
                        event.data shouldBe """{"seriesId":9,"eventId":4,"status":"COMPLETE"}"""
                    }
                    emitter.cancel()
                }
            }
        }
    })
