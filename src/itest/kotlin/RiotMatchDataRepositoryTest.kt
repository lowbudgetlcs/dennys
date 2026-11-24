import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.riotmatchdata.RiotMatchDataRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

private val MATCH =
    Json
        .parseToJsonElement(
            """
            {
              "metadata": { "matchId": "NA1_5102531894", "participants": ["p1", "p2"] },
              "info": {
                "gameDuration": 1834,
                "participants": [ { "puuid": "p1", "kills": 7, "challenges": { "kda": 4.5 } } ],
                "teams": [ { "teamId": 100, "win": true, "objectives": { "atakhan": { "first": true, "kills": 1 } } } ],
                "someFieldRiotAddsLater": [1, 2.25, null, "x"]
              }
            }
            """.trimIndent(),
        ).jsonObject

private val TIMELINE =
    Json
        .parseToJsonElement(
            """{ "metadata": { "matchId": "NA1_5102531894" }, "info": { "frameInterval": 60000, "frames": [] } }""",
        ).jsonObject

class RiotMatchDataRepositoryTest :
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
        val repo = RiotMatchDataRepository(dsl)
        lateinit var game: Game

        beforeSpec {
            val event =
                EventRepository(dsl).insert(
                    NewEvent(
                        name = "Test".toEventName(),
                        description = "Testing Riot match data.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.REGULAR_SEASON),
                    ),
                    riotTournamentId = 1.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert event.")
            val teams = TeamRepository(dsl)
            val team1 = teams.insert(NewTeam("Team 1".toTeamName())) ?: throw Exception("Failed to insert team 1.")
            val team2 = teams.insert(NewTeam("Team 2".toTeamName())) ?: throw Exception("Failed to insert team 2.")
            val series =
                SeriesRepository(dsl).insert(
                    NewSeries(
                        eventId = event.id,
                        totalGames = 3,
                        participantIds = Pair(team1.id, team2.id),
                        eventStage = EventStage.REGULAR_SEASON,
                    ),
                ) ?: throw Exception("Failed to insert series.")
            game =
                GameRepository(dsl).insert(NewGame(series.id, null, "NA1_5102531894".toRiotMatchId(), null))
                    ?: throw Exception("Failed to insert game.")
        }

        test("round-trips the match and timeline unchanged, including fields Denny's has no model for") {
            repo.save(game.id, MATCH, TIMELINE).shouldNotBeNull()

            val stored = repo.getByGameId(game.id).shouldNotBeNull()
            stored.match shouldBe MATCH
            stored.timeline shouldBe TIMELINE
        }

        test("saving again replaces the stored payloads") {
            repo.save(game.id, MATCH, TIMELINE)

            val replaced = repo.save(game.id, TIMELINE, null).shouldNotBeNull()

            replaced.match shouldBe TIMELINE
            replaced.timeline.shouldBeNull()
            repo.getByGameId(game.id)?.timeline.shouldBeNull()
        }

        test("a game without Riot match data returns null") {
            repo.getByGameId(Int.MAX_VALUE.toGameId()).shouldBeNull()
        }
    })
