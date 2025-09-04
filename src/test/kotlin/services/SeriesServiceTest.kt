package services

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.team.toTeamName
import com.lowbudgetlcs.domain.services.series.SeriesService
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant

class SeriesServiceTest : StringSpec({
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val service = SeriesService(seriesRepo, teamRepo)

    beforeTest { clearAllMocks() }

    val event = Event(
        id = 0.toEventId(),
        name = "Test",
        description = "",
        riotTournamentId = 0.toRiotTournamentId(),
        createdAt = Instant.now(),
        startDate = Instant.now(),
        endDate = Instant.now().plusSeconds(6L),
        status = EventStatus.ACTIVE,
        eventGroupId = null
    )
    val participatingTeams = listOf(
        Team(
            id = 1.toTeamId(), name = "Test".toTeamName(), eventId = event.id, logoName = null
        ), Team(
            id = 2.toTeamId(), name = "Test 2".toTeamName(), eventId = event.id, logoName = null
        )
    )
    val expectedSeries = Series(
        id = SeriesId(1),
        gamesToWin = 3,
        eventId = event.id,
        participants = participatingTeams.map { it.id },
        result = null
    )

    val newSeries = NewSeries(
        eventId = event.id, participantIds = participatingTeams.map { it.id }, gamesToWin = 3
    )
    "createSeries succeeds for valid input" {

        every { seriesRepo.insert(newSeries) } returns expectedSeries
        every { teamRepo.getById(participatingTeams[0].id) } returns participatingTeams[0]
        every { teamRepo.getById(participatingTeams[1].id) } returns participatingTeams[1]

        service.createSeries(newSeries) shouldBe expectedSeries

        verify(exactly = 1) { seriesRepo.insert(newSeries) }
    }

    "createSeries fails for 0 games to win" {
        shouldThrow<IllegalArgumentException> {
            service.createSeries(
                newSeries.copy(gamesToWin = 0)
            )
        }
        // repo.insert should never be called
        verify(exactly = 0) { seriesRepo.insert(any()) }
    }

    "getAllTeams returns repo data" {
        val series = listOf(
            Series(SeriesId(1), EventId(1), 3, participatingTeams.map { it.id }, null), Series(
                SeriesId(2), EventId(1), 3, participatingTeams.map { it.id }, null
            )
        )

        every { seriesRepo.getAllByEventId(EventId(1)) } returns series

        val result = service.getAllSeriesFromEvent(EventId(1))
        result.map { it.id } shouldContainExactly listOf(SeriesId(1), SeriesId(2))

        verify(exactly = 1) { seriesRepo.getAllByEventId(EventId(1)) }
    }

    "getSeries throws for unknown id" {
        val id = SeriesId(999)
        every { seriesRepo.getById(id) } returns null

        val ex = shouldThrow<NoSuchElementException> { service.getSeries(id) }
        ex.message shouldBe "Series not found"

        verify(exactly = 1) { seriesRepo.getById(id) }
    }
})