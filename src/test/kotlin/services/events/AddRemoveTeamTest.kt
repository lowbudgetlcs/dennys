package services.events

import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.toEvent
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.events.toEventWithTeams
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.toTeam
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.team.toTeamName
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.gateways.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.IEventRepository
import com.lowbudgetlcs.repositories.ISeriesRepository
import com.lowbudgetlcs.repositories.ITeamRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.temporal.ChronoUnit

<<<<<<< Updated upstream
class AddRemoveTeamTest : FunSpec({
    val eventRepo = mockk<IEventRepository>()
    val tournamentGate = mockk<IRiotTournamentGateway>()
    val teamRepo = mockk<ITeamRepository>()
    val service = EventService(eventRepo, tournamentGate, teamRepo, mockk<ISeriesRepository>())
    val start = Instant.now()
    val end = Instant.now().plusSeconds(3600L)
    val newEvent = NewEvent(
        name = "Test", description = "This is a test.", startDate = start, endDate = end, status = EventStatus.ACTIVE
    )
    val expectedEvent = newEvent.toEvent(
        id = 0.toEventId(),
        createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        riotTournamentId = 9999.toRiotTournamentId(),
    )
    val newTeam = NewTeam(
        name = "TEST".toTeamName()
    )
    val expectedTeam = newTeam.toTeam(0.toTeamId(), eventId = expectedEvent.id)
    val expectedEventWithTeams = expectedEvent.toEventWithTeams(listOf(expectedTeam))

    test("addTeam() associates a team with an event") {
        every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
        every { teamRepo.getById(expectedTeam.id) } returns expectedTeam
        every {
            teamRepo.updateEventId(
                expectedTeam.id, expectedEvent.id
            )
        } returns expectedTeam
        every { teamRepo.getAll() } returns listOf(expectedTeam)
        val event = service.addTeam(expectedEvent.id, expectedTeam.id)
        event.shouldNotBeNull()
        event shouldBe expectedEventWithTeams
    }

    test("remove() sets eventId to null") {
        every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
        every { teamRepo.getById(expectedTeam.id) } returns expectedTeam
        every {
            teamRepo.updateEventId(
                expectedTeam.id, null
            )
        } returns expectedTeam.copy(eventId = null)
        every { teamRepo.getAll() } returns listOf(expectedTeam)
        val event = service.addTeam(expectedEvent.id, expectedTeam.id)
        event.shouldNotBeNull()
        event shouldBe expectedEventWithTeams
    }
})
=======
class AddRemoveTeamTest :
        FunSpec({
            val eventRepo = mockk<IEventRepository>()
            val tournamentGate = mockk<ITournamentGateway>()
            val teamRepo = mockk<ITeamRepository>()
            val seriesRepo = mockk<ISeriesRepository>()
            val service = EventService(eventRepo, tournamentGate, teamRepo, seriesRepo)
            val start = Instant.now()
            val end = Instant.now().plusSeconds(3600L)
            val newEvent =
                    NewEvent(
                            name = "Test",
                            description = "This is a test.",
                            startDate = start,
                            endDate = end,
                            status = EventStatus.ACTIVE
                    )
            val expectedEvent =
                    newEvent.toEvent(
                            id = 0.toEventId(),
                            createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
                            tournamentId = 9999.toTournamentId(),
                    )
            val newTeam = NewTeam(name = "TEST".toTeamName())
            val expectedTeam = newTeam.toTeam(0.toTeamId(), eventId = expectedEvent.id)
            val expectedEventWithTeams = expectedEvent.toEventWithTeams(listOf(expectedTeam))

            test("addTeam() associates a team with an event") {
                every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
                every { teamRepo.getById(expectedTeam.id) } returns expectedTeam
                every { teamRepo.updateEventId(expectedTeam.id, expectedEvent.id) } returns
                        expectedTeam
                every { teamRepo.getAll() } returns listOf(expectedTeam)
                val event = service.addTeam(expectedEvent.id, expectedTeam.id)
                event.shouldNotBeNull()
                event shouldBe expectedEventWithTeams
            }

            test("remove() sets eventId to null") {
                every { eventRepo.getById(expectedEvent.id) } returns expectedEvent
                every { teamRepo.getById(expectedTeam.id) } returns expectedTeam
                every { teamRepo.updateEventId(expectedTeam.id, null) } returns
                        expectedTeam.copy(eventId = null)
                every { teamRepo.getAll() } returns listOf(expectedTeam)
                val event = service.addTeam(expectedEvent.id, expectedTeam.id)
                event.shouldNotBeNull()
                event shouldBe expectedEventWithTeams
            }
        })
>>>>>>> Stashed changes
