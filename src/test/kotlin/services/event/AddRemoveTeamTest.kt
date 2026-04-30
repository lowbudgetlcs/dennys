package services.events

import com.lowbudgetlcs.domain.event.core.EventService
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.models.toEvent
import com.lowbudgetlcs.domain.event.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.event.core.model.types.toEventId
import com.lowbudgetlcs.domain.event.core.model.types.toEventName
import com.lowbudgetlcs.domain.event.core.model.toEventWithTeams
import com.lowbudgetlcs.domain.event.core.model.toRiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.toTeam
import com.lowbudgetlcs.domain.team.core.model.toTeamId
import com.lowbudgetlcs.domain.team.core.model.toTeamName
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.domain.event.core.port.IEventRepository
import com.lowbudgetlcs.domain.series.core.port.ISeriesRepository
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class AddRemoveTeamTest :
    FunSpec({
        val eventRepo = mockk<IEventRepository>()
        val tournamentGate = mockk<IRiotTournamentGateway>()
        val teamRepo = mockk<ITeamRepository>()
        val service = EventService(eventRepo, tournamentGate, teamRepo, mockk<ISeriesRepository>())
        val start = Instant.now()
        val end = Instant.now().plusSeconds(3600L)
        val newEvent =
            NewEvent(
                name = "Test".toEventName(),
                description = "This is a test.".toEventDescription(),
                startDate = start,
                endDate = end,
                status = EventStatus.ACTIVE,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val expectedEvent =
            newEvent.toEvent(
                id = 0.toEventId(),
                createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                riotTournamentId = 9999.toRiotTournamentId(),
            )
        val newTeam =
            NewTeam(
                name = "TEST".toTeamName(),
            )
        val expectedTeam = newTeam.toTeam(0.toTeamId(), eventId = expectedEvent.id)
        val expectedEventWithTeams = expectedEvent.toEventWithTeams(listOf(expectedTeam))

        test("addTeam() associates a team with an event") {
            every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
            every { teamRepo.getById(expectedTeam.id) } returns expectedTeam
            every {
                teamRepo.update(any(), any())
            } returns expectedTeam
            every { teamRepo.getByEventId(expectedEvent.id) } returns listOf(expectedTeam)
            val event = service.addTeam(expectedEvent.id, expectedTeam.id)
            event.shouldNotBeNull()
            event shouldBe expectedEventWithTeams
        }

        test("remove() sets eventId to null") {
            every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
            every { teamRepo.getById(expectedTeam.id) } returns expectedTeam
            every {
                teamRepo.update(
                    any(),
                    any(),
                )
            } returns expectedTeam.copy(eventId = null)
            every { teamRepo.getByEventId(expectedEvent.id) } returns listOf(expectedTeam)
            val event = service.addTeam(expectedEvent.id, expectedTeam.id)
            event.shouldNotBeNull()
            event shouldBe expectedEventWithTeams
        }
    })
