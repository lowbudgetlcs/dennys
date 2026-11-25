package com.lowbudgetlcs.api.routes.v1.stream

import com.lowbudgetlcs.domain.LiveEvents
import io.ktor.server.routing.Route
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlin.time.Duration.Companion.seconds

fun Route.streamRoutesV1(liveEvents: LiveEvents) {
    sse("/stream") {
        // Under the 60 s idle timeout of nginx and most proxies.
        heartbeat { period = 25.seconds }
        liveEvents.events.collect { send(ServerSentEvent(it.data.toString(), event = it.name)) }
    }
}
