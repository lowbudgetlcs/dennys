package services

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.filterByCompletion
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamId
import com.lowbudgetlcs.domain.team.models.toTeamName
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
                name = "Test".toEventName(),
                description = "Testing 2".toEventDescription(),
                riotTournamentId = 0.toRiotTournamentId(),
                createdAt = Instant.now(),
                startDate = Instant.now(),
                endDate = Instant.now().plusSeconds(6L),
                status = EventStatus.ACTIVE,
                eventGroupId = null,
                eventStages = setOf(EventStage.REGULAR_SEASON),
            )
        val team1 =
            Team(
                id = 1.toTeamId(),
                name = "testing".toTeamName(),
                logo = "abcd",
                eventId = event.id,
            )
        val team2 =
            Team(
                id = 2.toTeamId(),
                name = "testing2".toTeamName(),
                logo = "abcddsa",
                eventId = event.id,
            )
        val participatingTeams =
            Pair(
                team1.id,
                team2.id,
            )
        val expectedSeries =
            Series(
                id = 1.toSeriesId(),
                totalGames = 3,
                eventId = event.id,
                participants = participatingTeams,
                result = null,
                eventStage = EventStage.REGULAR_SEASON,
                completed = false,
                completedAt = null,
                reopenedAt = null,
            )

        val newSeries =
            NewSeries(
                eventId = event.id,
                participantIds = participatingTeams,
                totalGames = 3,
                eventStage = EventStage.REGULAR_SEASON,
            )
        "createSeries succeeds for valid input" {
            every { seriesRepo.insert(newSeries) } returns expectedSeries
            every { teamRepo.getById(participatingTeams.first) } returns team1
            every { teamRepo.getById(participatingTeams.second) } returns team2

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
                        participants = participatingTeams,
                        result = null,
                        eventStage = EventStage.REGULAR_SEASON,
                        completed = false,
                        completedAt = null,
                        reopenedAt = null,
                    ),
                    Series(
                        id = SeriesId(2),
                        eventId = EventId(1),
                        totalGames = 3,
                        participants = participatingTeams,
                        result = null,
                        eventStage = EventStage.REGULAR_SEASON,
                        completed = false,
                        completedAt = null,
                        reopenedAt = null,
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

        val openSeries =
            Series(
                id = SeriesId(1),
                eventId = EventId(1),
                totalGames = 3,
                participants = participatingTeams,
                result = null,
                eventStage = EventStage.PLAYOFFS,
                completed = false,
                completedAt = null,
                reopenedAt = null,
            )
        val closedSeries =
            openSeries.copy(
                id = SeriesId(2),
                completed = true,
                completedAt = Instant.parse("2026-07-14T23:12:04Z"),
            )
        val bothSeries = listOf(openSeries, closedSeries)

        "filterByCompletion returns only open series for completed = false" {
            bothSeries.filterByCompletion(
                SeriesQuery(teamIds = null, eventStage = null, completed = false),
            ) shouldContainExactly listOf(openSeries)
        }

        "filterByCompletion returns only closed series for completed = true" {
            bothSeries.filterByCompletion(
                SeriesQuery(teamIds = null, eventStage = null, completed = true),
            ) shouldContainExactly listOf(closedSeries)
        }

        "filterByCompletion is unfiltered when completed is omitted" {
            bothSeries.filterByCompletion(
                SeriesQuery(teamIds = null, eventStage = null),
            ) shouldContainExactly bothSeries

            bothSeries.filterByCompletion(null) shouldContainExactly bothSeries
        }

        "filterByCompletion distinguishes two series with identical participants and stage" {
            openSeries.participants shouldBe closedSeries.participants
            openSeries.eventStage shouldBe closedSeries.eventStage

            bothSeries.filterByCompletion(
                SeriesQuery(teamIds = null, eventStage = null, completed = false),
            ) shouldContainExactly listOf(openSeries)
        }
    })
