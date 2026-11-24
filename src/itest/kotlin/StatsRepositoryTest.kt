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
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.stats.models.StatsQuery
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
import com.lowbudgetlcs.repositories.stats.StatsRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
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

private val Y_PUUID = "y".repeat(78)
private val SUB_PUUID = "s".repeat(78)
private val B_PUUID = "b".repeat(78)
private val C_PUUID = "c".repeat(78)

private fun lobby(
    bluePuuid: String,
    blueChampion: Int,
    redPuuid: String,
): JsonObject =
    Json
        .parseToJsonElement(
            """
            {
              "info": {
                "participants": [
                  { "puuid": "$bluePuuid", "teamId": 100, "win": true, "championId": $blueChampion, "kills": 3 },
                  { "puuid": "$redPuuid", "teamId": 200, "win": false, "championId": 1, "kills": 1 }
                ],
                "teams": [{ "teamId": 100, "win": true }, { "teamId": 200, "win": false }]
              }
            }
            """.trimIndent(),
        ).jsonObject

class StatsRepositoryTest :
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
        val stats = StatsRepository(dsl)
        lateinit var eventOne: Event
        lateinit var teamA: Team
        lateinit var teamC: Team
        lateinit var regularOne: Game
        lateinit var playoffOne: Game
        lateinit var regularTwo: Game
        lateinit var playerY: Player

        beforeSpec {
            val groups = EventGroupRepository(dsl)
            val events = EventRepository(dsl)

            fun event(name: String): Event {
                val group =
                    groups.insert(NewEventGroup("$name Season".toEventGroupName()))
                        ?: throw Exception("Failed to insert event group.")
                return events.insert(
                    NewEvent(
                        name = name.toEventName(),
                        description = "Testing stats rows.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.REGULAR_SEASON, EventStage.PLAYOFFS),
                        eventGroupId = group.id,
                    ),
                    riotTournamentId = 1.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert event.")
            }
            eventOne = event("One")
            val eventTwo = event("Two")

            val teams = TeamRepository(dsl)
            teamA = teams.insert(NewTeam("Team A".toTeamName())) ?: throw Exception("Failed to insert team.")
            val teamB = teams.insert(NewTeam("Team B".toTeamName())) ?: throw Exception("Failed to insert team.")
            teamC = teams.insert(NewTeam("Team C".toTeamName())) ?: throw Exception("Failed to insert team.")

            playerY =
                PlayerRepository(dsl).insert(NewPlayer("Player Y".toPlayerName()))
                    ?: throw Exception("Failed to insert player.")
            AccountRepository(dsl).insert(NewAccount(Puuid(Y_PUUID), playerY.id))

            val series = SeriesRepository(dsl)
            val games = GameRepository(dsl)
            val matchData = RiotMatchDataRepository(dsl)

            fun game(
                event: Event,
                stage: EventStage,
                opponent: Team,
                matchId: String,
                payload: JsonObject,
            ): Game {
                val s =
                    series.insert(
                        NewSeries(
                            eventId = event.id,
                            eventStage = stage,
                            totalGames = 3,
                            participantIds = Pair(teamA.id, opponent.id),
                        ),
                    )
                        ?: throw Exception("Failed to insert series.")
                val g =
                    games.insert(NewGame(s.id, null, matchId.toRiotMatchId(), GameResult(teamA.id, opponent.id)))
                        ?: throw Exception("Failed to insert game.")
                matchData.save(g.id, payload, null)
                return g
            }
            regularOne = game(eventOne, EventStage.REGULAR_SEASON, teamB, "NA1_1", lobby(Y_PUUID, 103, B_PUUID))
            playoffOne = game(eventOne, EventStage.PLAYOFFS, teamC, "NA1_2", lobby(SUB_PUUID, 103, C_PUUID))
            regularTwo = game(eventTwo, EventStage.REGULAR_SEASON, teamB, "NA1_3", lobby(Y_PUUID, 99, B_PUUID))
        }

        fun players(
            query: StatsQuery,
            limit: Int = 100,
            offset: Int = 0,
        ) = stats.getPlayerGames(query, limit, offset).map { it.gameId to it.puuid }

        fun teams(query: StatsQuery) = stats.getTeamGames(query, 100, 0).map { it.gameId to it.teamId }

        test("playerId and eventGroupId return only that player's games in that season") {
            val seasonOne = eventOne.eventGroupId.shouldNotBeNull()
            players(StatsQuery(eventGroupId = seasonOne, playerId = playerY.id)) shouldBe
                listOf(regularOne.id to Y_PUUID)
            players(StatsQuery(playerId = playerY.id)) shouldBe
                listOf(regularTwo.id to Y_PUUID, regularOne.id to Y_PUUID)
        }

        test("eventId, stage and teamId combine") {
            val query = StatsQuery(eventId = eventOne.id, stage = EventStage.PLAYOFFS, teamId = teamC.id)
            players(query) shouldBe listOf(playoffOne.id to C_PUUID)
            teams(query) shouldBe listOf(playoffOne.id to teamC.id)
            players(query.copy(stage = EventStage.REGULAR_SEASON)) shouldBe emptyList()
        }

        test("championId filters player rows") {
            players(StatsQuery(championId = 103)) shouldBe listOf(playoffOne.id to SUB_PUUID, regularOne.id to Y_PUUID)
        }

        test("playerId on team rows keeps the side that player played on") {
            teams(StatsQuery(playerId = playerY.id)) shouldBe
                listOf(regularTwo.id to teamA.id, regularOne.id to teamA.id)
        }

        test("no filter returns every row, newest game first, and pages stitch back together") {
            val all = players(StatsQuery())
            all.map { it.first } shouldBe
                listOf(regularTwo.id, regularTwo.id, playoffOne.id, playoffOne.id, regularOne.id, regularOne.id)
            players(StatsQuery(), limit = 4) + players(StatsQuery(), limit = 4, offset = 4) shouldBe all
            teams(StatsQuery()).size shouldBe 6
        }
    })
