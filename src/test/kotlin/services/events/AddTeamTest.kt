package services.events

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.gateways.ITournamentGateway
import com.lowbudgetlcs.repositories.IEventRepository
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class AddTeamTest : FunSpec({
    val eventRepo = mockk<IEventRepository>()
    val service = EventService(eventRepo, mockk<ITournamentGateway>())
    val start = Instant.now()
    val end = start.plusSeconds(40_000L)
    val testEvent = Event(
        id = 0.toEventId(),
        createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        tournamentId = 9999.toTournamentId(),
        name = "Test",
        description = "This is a test.",
        eventGroupId = null,
        startDate = start,
        endDate = end,
        status = EventStatus.NOT_STARTED,
    )

    test("") {}
})
