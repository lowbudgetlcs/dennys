package routes

import com.lowbudgetlcs.api.routes.v1.event.eventRoutesV1
import com.lowbudgetlcs.domain.event.IEventService
import com.lowbudgetlcs.domain.event.models.EventQuery
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

class EventSeriesFilterRouteTest :
    StringSpec({
        "?status= on an event's series reaches the service as a series status" {
            val eventService = mockk<IEventService>()
            val query = slot<SeriesQuery>()
            every { eventService.getEventWithSeries(EventId(3), capture(query)) } throws
                IllegalStateException("captured")

            testApplication {
                install(Resources)
                application { routing { eventRoutesV1(eventService, mockk()) } }

                client.get("/event/3/series?status=COMPLETE")
            }

            query.captured.status shouldBe SeriesStatus.COMPLETE
        }

        "?status= on the event list still filters by event status" {
            val eventService = mockk<IEventService>()
            val query = slot<EventQuery>()
            every { eventService.getAllEvents(capture(query)) } returns emptyList()

            testApplication {
                install(Resources)
                application { routing { eventRoutesV1(eventService, mockk()) } }

                client.get("/event?status=ACTIVE")
            }

            query.captured.status shouldBe EventStatus.ACTIVE
        }
    })
