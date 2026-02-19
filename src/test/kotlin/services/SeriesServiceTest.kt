package services

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.team.toTeamName
import com.lowbudgetlcs.domain.services.series.SeriesService
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
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

class SeriesServiceTest :
    StringSpec({
        val gameRepo = mockk<IGameRepository>(relaxed = false)
        val eventRepo = mockk<IEventRepository>(relaxed = false)
        val teamRepo = mockk<ITeamRepository>(relaxed = false)
        val seriesRepo = mockk<ISeriesRepository>(relaxed = false)
        val tournamentGateway = mockk<IRiotTournamentGateway>(relaxed = false)
        val service = SeriesService(gameRepo, seriesRepo, eventRepo, teamRepo, tournamentGateway)

        beforeTest { clearAllMocks() }

        val event =
            Event(
                id = 0.toEventId(),
                name = "Test",
                description = "",
                riotTournamentId = 0.toRiotTournamentId(),
                createdAt = Instant.now(),
                startDate = Instant.now(),
                endDate = Instant.now().plusSeconds(6L),
                status = EventStatus.ACTIVE,
                eventGroupId = null,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val participatingTeams =
            listOf(
                Team(
                    id = 1.toTeamId(),
                    name = "Test".toTeamName(),
                    eventId = event.id,
                    logoName = null,
                ),
                Team(
                    id = 2.toTeamId(),
                    name = "Test 2".toTeamName(),
                    eventId = event.id,
                    logoName = null,
                ),
            )
        val expectedSeries =
            Series(
                id = SeriesId(1),
                totalGames = 3,
                eventId = event.id,
                participants = participatingTeams.map { it.id },
                result = null,
                eventStage = EventStage.REGULAR_SEASON,
            )

        val newSeries =
            NewSeries(
                eventId = event.id,
                participantIds = participatingTeams.map { it.id },
                totalGames = 3,
                eventStage = EventStage.REGULAR_SEASON,
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
                    newSeries.copy(totalGames = 0),
                )
            }
            // repo.insert should never be called
            verify(exactly = 0) { seriesRepo.insert(any()) }
        }

        "getAllTeams returns repo data" {
            val series =
                listOf(
                    Series(
                        id = SeriesId(1),
                        eventId = EventId(1),
                        totalGames = 3,
                        participants = participatingTeams.map { it.id },
                        result = null,
                        eventStage = EventStage.REGULAR_SEASON,
                    ),
                    Series(
                        id = SeriesId(2),
                        eventId = EventId(1),
                        totalGames = 3,
                        participants = participatingTeams.map { it.id },
                        result = null,
                        eventStage = EventStage.REGULAR_SEASON,
                    ),
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
