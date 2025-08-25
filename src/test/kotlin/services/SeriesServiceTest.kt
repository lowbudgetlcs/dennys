package services

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.services.SeriesService
import com.lowbudgetlcs.repositories.ISeriesRepository
import com.lowbudgetlcs.repositories.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SeriesServiceTest : StringSpec({
    val teamRepo = mockk<ITeamRepository>(relaxed = false)
    val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
    val service = SeriesService(seriesRepo, teamRepo)

    beforeTest { clearAllMocks() }

    "createSeries succeeds for valid input" {
        val participatingTeams = listOf(TeamId(1), TeamId(2))
        val eventId = EventId(3)
        val created = Series(
            id = SeriesId(1), gamesToWin = 3, eventId = eventId, participants = participatingTeams,
            result = null
        )

        val newSeries = NewSeries(
            eventId = eventId, participantIds = participatingTeams, gamesToWin = 3
        )

        every { seriesRepo.insert(newSeries) } returns created

        service.createSeries(newSeries) shouldBe created

        verify(exactly = 1) { seriesRepo.insert(newSeries) }
    }

    "createSeries fails for 0 games to win" {
        shouldThrow<IllegalArgumentException> {
            service.createSeries(
                NewSeries(
                    eventId = EventId(1), participantIds = listOf(TeamId(1), TeamId(2)), gamesToWin = 0
                )
            )
        }
        // repo.insert should never be called
        verify(exactly = 0) { seriesRepo.insert(any()) }
    }

    "getAllTeams returns repo data" {
        val series = listOf(
            Series(SeriesId(1), EventId(1), 3, null, null), Series(SeriesId(2), EventId(1), 3, TeamId(1), TeamId(2))
        )

        every { seriesRepo.getAllFromEvent(EventId(1)) } returns series

        val result = service.getAllSeriesFromEvent(EventId(1))
        result.map { it.id } shouldContainExactly listOf(SeriesId(1), SeriesId(2))

        verify(exactly = 1) { seriesRepo.getAllFromEvent(EventId(1)) }
    }

    "getSeries throws for unknown id" {
        val id = SeriesId(999)
        every { seriesRepo.getById(id) } returns null

        val ex = shouldThrow<NoSuchElementException> { service.getSeries(id) }
        ex.message shouldBe "Team not found"

        verify(exactly = 1) { seriesRepo.getById(id) }
    }
})