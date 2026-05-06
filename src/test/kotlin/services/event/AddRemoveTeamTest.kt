package services.event

import com.lowbudgetlcs.domain.division.core.services.EventService
import com.lowbudgetlcs.domain.division.core.model.NewEvent
import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.model.toEvent
import com.lowbudgetlcs.domain.division.core.model.toEventWithTeams
import com.lowbudgetlcs.domain.division.core.model.toRiotTournamentId
import com.lowbudgetlcs.domain.division.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.division.core.model.types.toEventId
import com.lowbudgetlcs.domain.division.core.model.types.toEventName
import com.lowbudgetlcs.domain.division.core.port.IEventRepository
import com.lowbudgetlcs.domain.series.core.SeriesService
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.toTeam
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import com.lowbudgetlcs.domain.team.core.model.types.toTeamName
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

class AddRemoveTeamTest() :
    FunSpec({
        val eventRepo = mockk<IEventRepository>()
        val tournamentGate = mockk<IRiotTournamentGateway>()
        val teamRepo = mockk<ITeamRepository>()
        val service = EventService(eventRepo, tournamentGate, teamRepo, mockk<SeriesService>())
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
            coEvery { eventRepo.getById(expectedEvent.id) } returns expectedEvent
            coEvery { teamRepo.getById(expectedTeam.id) } returns expectedTeam
            coEvery {
                teamRepo.update(any(), any())
            } returns expectedTeam
            coEvery { teamRepo.getByEventId(expectedEvent.id) } returns listOf(expectedTeam)
            val event = service.addTeam(expectedEvent.id, expectedTeam.id)
            event.shouldNotBeNull()
            event shouldBe expectedEventWithTeams
        }

        test("remove() sets eventId to null") {
            coEvery { eventRepo.getById(expectedEvent.id) } returns expectedEvent
            coEvery { teamRepo.getById(expectedTeam.id) } returns expectedTeam
            coEvery {
                teamRepo.update(
                    any(),
                    any(),
                )
            } returns expectedTeam.copy(eventId = null)
            coEvery { teamRepo.getByEventId(expectedEvent.id) } returns listOf(expectedTeam)
            val event = service.addTeam(expectedEvent.id, expectedTeam.id)
            event.shouldNotBeNull()
            event shouldBe expectedEventWithTeams
        }
    })
