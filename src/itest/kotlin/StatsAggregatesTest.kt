import com.lowbudgetlcs.domain.InvalidRequestException
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.models.NewEventGroup
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupName
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.account.AccountRepository
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.eventgroup.EventGroupRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.player.PlayerRepository
import com.lowbudgetlcs.repositories.riotmatchdata.RiotMatchDataRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.stats.StatsAggregateRepository
import com.lowbudgetlcs.repositories.stats.StatsRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

private data class Seat(
    val puuid: String,
    val champion: Int,
    val kills: Int,
)

private fun lobby(
    blue: List<Seat>,
    red: List<Seat>,
    blueWins: Boolean,
    durationSeconds: Int = 1800,
    blueBans: List<Int> = emptyList(),
): JsonObject {
    fun seats(
        side: Int,
        win: Boolean,
        seats: List<Seat>,
    ) = seats.joinToString(",") {
        """{ "puuid": "${it.puuid}", "teamId": $side, "win": $win, "championId": ${it.champion}, "kills": ${it.kills} }"""
    }

    fun team(
        side: Int,
        win: Boolean,
        seats: List<Seat>,
        bans: List<Int>,
    ) = """
        { "teamId": $side, "win": $win,
          "bans": [${bans.mapIndexed { i, c -> """{ "championId": $c, "pickTurn": ${i + 1} }""" }.joinToString(",")}],
          "objectives": { "champion": { "first": $win, "kills": ${seats.sumOf { it.kills }} } } }
        """
    return Json
        .parseToJsonElement(
            """
            {
              "info": {
                "gameDuration": $durationSeconds,
                "participants": [${seats(100, blueWins, blue)}, ${seats(200, !blueWins, red)}],
                "teams": [${team(100, blueWins, blue, blueBans)}, ${team(200, !blueWins, red, emptyList())}]
              }
            }
            """.trimIndent(),
        ).jsonObject
}

class StatsAggregatesTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(
                    MountableFile.forClasspathResource("sql"),
                    "/docker-entrypoint-initdb.d/",
                )
            }
        val ds =
            install(JdbcDatabaseContainerSpecExtension(postgres)) {
                maximumPoolSize = 1
            }
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val aggregates = StatsAggregateRepository(dsl)
        lateinit var season: Event
        lateinit var teamA: Team
        lateinit var teamB: Team
        lateinit var teamC: Team
        lateinit var veteran: Player
        lateinit var joiner: Player
        lateinit var benchSub: Player
        lateinit var rival: Player
        lateinit var switcher: Player

        beforeSpec {
            val events = EventRepository(dsl)

            fun event(name: String): Event {
                val group =
                    EventGroupRepository(dsl).insert(NewEventGroup("$name Season".toEventGroupName()))
                        ?: throw Exception("Failed to insert event group.")
                return events.insert(
                    NewEvent(
                        name = name.toEventName(),
                        description = "Testing stats aggregates.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.REGULAR_SEASON, EventStage.PLAYOFFS),
                        eventGroupId = group.id,
                    ),
                    riotTournamentId = 1.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert event.")
            }
            season = event("Current")
            val lastSeason = event("Previous")

            val teams = TeamRepository(dsl)
            teamA = teams.insert(NewTeam("Team A".toTeamName())) ?: throw Exception("Failed to insert team.")
            teamB = teams.insert(NewTeam("Team B".toTeamName())) ?: throw Exception("Failed to insert team.")
            teamC = teams.insert(NewTeam("Team C".toTeamName())) ?: throw Exception("Failed to insert team.")

            val players = PlayerRepository(dsl)
            val accounts = AccountRepository(dsl)

            fun player(name: String): Player {
                val player =
                    players.insert(NewPlayer(name.toPlayerName())) ?: throw Exception("Failed to insert player.")
                accounts.insert(NewAccount(Puuid(name.first().toString().repeat(78)), player.id))
                return player
            }
            veteran = player("Veteran")
            joiner = player("Joiner")
            benchSub = player("Bench")
            rival = player("Rival")
            switcher = player("Switcher")

            fun seat(
                player: Player,
                champion: Int,
                kills: Int,
            ) = Seat(
                player.name.value
                    .first()
                    .toString()
                    .repeat(78),
                champion,
                kills,
            )

            val seriesRepo = SeriesRepository(dsl)
            val games = GameRepository(dsl)
            val matchData = RiotMatchDataRepository(dsl)
            var matchNumber = 0

            fun series(
                event: Event,
                stage: EventStage,
                blueTeam: Team,
                redTeam: Team,
            ): Series =
                seriesRepo.insert(
                    NewSeries(
                        eventId = event.id,
                        eventStage = stage,
                        totalGames = 3,
                        participantIds = Pair(blueTeam.id, redTeam.id),
                    ),
                ) ?: throw Exception("Failed to insert series.")

            fun game(
                series: Series,
                blueTeam: Team,
                redTeam: Team,
                payload: JsonObject,
                blueWins: Boolean,
            ) {
                val result = if (blueWins) GameResult(blueTeam.id, redTeam.id) else GameResult(redTeam.id, blueTeam.id)
                val game =
                    games.insert(NewGame(series.id, null, "NA1_${++matchNumber}".toRiotMatchId(), result))
                        ?: throw Exception("Failed to insert game.")
                matchData.save(game.id, payload, null)
            }

            fun close(
                series: Series,
                winner: Team?,
                loser: Team?,
            ) {
                val result = if (winner != null && loser != null) SeriesResult(winner.id, loser.id) else null
                seriesRepo.setStatus(series.id, SeriesStatus.COMPLETE, result, doubleLoss = result == null)
            }

            // A wins 2-1; the bench sub plays only the lost game 1.
            val aWinsTwoOne = series(season, EventStage.REGULAR_SEASON, teamA, teamB)
            game(
                aWinsTwoOne,
                teamA,
                teamB,
                lobby(
                    listOf(seat(veteran, 103, 5), seat(benchSub, 84, 9)),
                    listOf(seat(rival, 1, 1)),
                    blueWins = false,
                    blueBans = listOf(55, 84),
                ),
                blueWins = false,
            )
            game(aWinsTwoOne, teamA, teamB, lobby(listOf(seat(veteran, 103, 6)), listOf(seat(rival, 1, 1)), true), true)
            game(aWinsTwoOne, teamA, teamB, lobby(listOf(seat(veteran, 103, 7)), listOf(seat(rival, 1, 1)), true), true)
            close(aWinsTwoOne, teamA, teamB)

            // The joiner's first series.
            val aBeatsC = series(season, EventStage.REGULAR_SEASON, teamA, teamC)
            game(
                aBeatsC,
                teamA,
                teamC,
                lobby(listOf(seat(veteran, 103, 2), seat(joiner, 2, 3)), listOf(seat(switcher, 3, 0)), true),
                true,
            )
            close(aBeatsC, teamA, teamC)

            // A double loss; the switcher plays for B here.
            val doubleLoss = series(season, EventStage.REGULAR_SEASON, teamA, teamB)
            game(
                doubleLoss,
                teamA,
                teamB,
                lobby(listOf(seat(joiner, 2, 4)), listOf(seat(rival, 1, 2), seat(switcher, 3, 0)), true),
                true,
            )
            close(doubleLoss, null, null)

            // Still in progress, with a short game a duration filter can drop.
            val inProgress = series(season, EventStage.PLAYOFFS, teamA, teamC)
            game(
                inProgress,
                teamA,
                teamC,
                lobby(listOf(seat(joiner, 2, 1)), listOf(seat(switcher, 3, 6)), false, durationSeconds = 200),
                false,
            )

            // B wins by forfeit with no games played.
            close(series(season, EventStage.REGULAR_SEASON, teamB, teamC), teamB, teamC)

            // Last season, which a season filter leaves out.
            val previous = series(lastSeason, EventStage.REGULAR_SEASON, teamA, teamB)
            game(previous, teamA, teamB, lobby(listOf(seat(veteran, 103, 10)), listOf(seat(rival, 1, 3)), false), false)
            close(previous, teamB, teamA)
        }

        fun players(
            query: StatsQuery = StatsQuery(eventGroupId = season.eventGroupId),
            sort: StatsSort = StatsSort("games"),
            limit: Int = 100,
            minGames: Int? = null,
        ) = aggregates.getPlayerAggregates(query, sort, limit, 0, minGames)

        fun List<PlayerAggregate>.of(player: Player) = single { it.playerId == player.id }

        fun List<PlayerAggregate>.record(player: Player) =
            of(player).let { Triple(it.seriesPlayed, it.seriesWon, it.seriesLost) }

        test("the season kills leaderboard sorts by total, and killsPerGame by average") {
            season.eventGroupId.shouldNotBeNull()
            players(sort = StatsSort("kills"), limit = 3).map { it.playerId to it.totals.kills } shouldBe
                listOf(veteran.id to 20L, benchSub.id to 9L, joiner.id to 8L)
            players(sort = StatsSort("killsPerGame"), limit = 2).map { it.playerId } shouldBe
                listOf(benchSub.id, veteran.id)
            players(sort = StatsSort("kills", descending = false), limit = 1).single().playerId shouldBe rival.id
            players().of(veteran).playerName shouldBe "Veteran"
        }

        test("a mid-season joiner's record counts only the series they played") {
            players().record(joiner) shouldBe Triple(3, 1, 1)
        }

        test("playing only the lost game of a 2-1 win counts the series as won") {
            players().record(benchSub) shouldBe Triple(1, 1, 0)
        }

        test("a double loss is lost for both sides and a forfeit counts for no one") {
            players().record(rival) shouldBe Triple(2, 0, 2)
            players().record(veteran) shouldBe Triple(2, 2, 0)
            players(StatsQuery()).record(veteran) shouldBe Triple(3, 2, 1)
        }

        test("teamId splits a player's record by the team they played for") {
            players().record(switcher) shouldBe Triple(3, 0, 2)
            players(StatsQuery(eventGroupId = season.eventGroupId, teamId = teamC.id)).record(switcher) shouldBe
                Triple(2, 0, 1)
            players(StatsQuery(eventGroupId = season.eventGroupId, teamId = teamB.id)).record(switcher) shouldBe
                Triple(1, 0, 1)
        }

        test("champions report picks, wins and bans, including one that was only banned") {
            val champions =
                aggregates.getChampionAggregates(
                    StatsQuery(eventGroupId = season.eventGroupId),
                    StatsSort("picks"),
                    100,
                    0,
                )
            val onlyBanned = champions.single { it.championId == 55 }
            onlyBanned.picks shouldBe 0
            onlyBanned.bans shouldBe 1
            onlyBanned.championName.shouldBeNull()
            champions.single { it.championId == 84 }.let { Triple(it.picks, it.wins, it.bans) } shouldBe Triple(1, 0, 1)
            champions.single { it.championId == 103 }.let { Triple(it.picks, it.wins, it.totals.kills) } shouldBe
                Triple(4, 3, 20L)
            champions.take(2).map { it.championId } shouldBe listOf(1, 103)
        }

        test("team totals group by team") {
            val teams =
                aggregates.getTeamAggregates(StatsQuery(eventGroupId = season.eventGroupId), StatsSort("wins"), 100, 0)
            val a = teams.first()
            a.teamId shouldBe teamA.id
            a.teamName shouldBe "Team A"
            Triple(a.games, a.wins, a.kills) shouldBe Triple(6, 4, 37L)
            a.firstBloods shouldBe 4L
        }

        test("minDurationSeconds drops a short game from aggregates and rows") {
            val longGames = StatsQuery(eventGroupId = season.eventGroupId, minDurationSeconds = 300)
            players(longGames).of(joiner).let { it.games to it.seriesPlayed } shouldBe (2 to 2)
            StatsRepository(dsl).getPlayerGames(longGames, 100, 0).none { it.gameDurationSeconds < 300 } shouldBe true
            aggregates
                .getTeamAggregates(
                    longGames,
                    StatsSort("games"),
                    100,
                    0,
                ).single { it.teamId == teamC.id }
                .games shouldBe
                1
        }

        test("minGames drops small samples before sorting and paging") {
            players(sort = StatsSort("killsPerGame"), limit = 1, minGames = 2).single().playerId shouldBe veteran.id
            players(minGames = 2).none { it.games < 2 } shouldBe true
            val inSeason = StatsQuery(eventGroupId = season.eventGroupId)
            aggregates.getTeamAggregates(inSeason, StatsSort("games"), 100, 0, minGames = 3).map { it.teamId } shouldBe
                listOf(teamA.id, teamB.id)

            fun champions(minPicks: Int) =
                aggregates.getChampionAggregates(inSeason, StatsSort("picks"), 100, 0, minPicks).map { it.championId }
            champions(1).contains(55) shouldBe false
            champions(2).contains(84) shouldBe false
            champions(2).contains(103) shouldBe true
        }

        test("an unknown sort is rejected before any SQL runs") {
            shouldThrow<InvalidRequestException> { players(sort = StatsSort("password")) }
            shouldThrow<InvalidRequestException> {
                aggregates.getChampionAggregates(StatsQuery(), StatsSort("bans; drop table games"), 10, 0)
            }
        }
    })
