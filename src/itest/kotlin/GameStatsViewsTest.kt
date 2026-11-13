import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.toEventDescription
import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.models.NewEventGroup
import com.lowbudgetlcs.domain.eventgroup.models.toEventGroupName
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
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
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.TournamentCodeRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.storage.tables.references.PLAYER_GAME_STATS_V1
import org.jooq.storage.tables.references.TEAM_GAME_STATS_V1
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

private val BLUE_PUUID = "b".repeat(78)
private val RED_PUUID = "r".repeat(78)

private fun match(
    blueObjectives: String,
    extraInfo: String = "",
) = Json
    .parseToJsonElement(
        """
        {
          "info": {
            $extraInfo
            "gameDuration": 1800,
            "participants": [
              {
                "puuid": "$BLUE_PUUID", "teamId": 100, "win": true, "championId": 103, "teamPosition": "MIDDLE",
                "kills": 5, "deaths": 1, "assists": 7, "totalMinionsKilled": 200, "neutralMinionsKilled": 30,
                "challenges": { "voidMonsterKill": 3 }
              },
              { "puuid": "$RED_PUUID", "teamId": 200, "win": false, "kills": 1 }
            ],
            "teams": [
              {
                "teamId": 100, "win": true,
                "bans": [
                  { "championId": 84, "pickTurn": 3 },
                  { "championId": -1, "pickTurn": 1 },
                  { "championId": 55, "pickTurn": 2 }
                ],
                "objectives": { "champion": { "first": true, "kills": 5 }, "tower": { "first": true, "kills": 9 } $blueObjectives }
              },
              { "teamId": 200, "win": false, "bans": [], "objectives": {} }
            ]
          }
        }
        """.trimIndent(),
    ).jsonObject

class GameStatsViewsTest :
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
        lateinit var blueTeam: Team
        lateinit var redTeam: Team
        lateinit var series: Series
        lateinit var withNewObjectives: Game
        lateinit var withoutNewObjectives: Game
        lateinit var swappedSides: Game
        var groupId = 0
        var bluePlayerId = 0

        beforeSpec {
            val group =
                EventGroupRepository(dsl).insert(NewEventGroup("Season".toEventGroupName()))
                    ?: throw Exception("Failed to insert event group.")
            groupId = group.id.value
            val event =
                EventRepository(dsl).insert(
                    NewEvent(
                        name = "Stats".toEventName(),
                        description = "Testing stats views.".toEventDescription(),
                        startDate = Instant.now(),
                        endDate = Instant.now().plusSeconds(3_600L),
                        status = EventStatus.ACTIVE,
                        eventStages = setOf(EventStage.PLAYOFFS),
                        eventGroupId = group.id,
                    ),
                    riotTournamentId = 1.toRiotTournamentId(),
                ) ?: throw Exception("Failed to insert event.")
            val teams = TeamRepository(dsl)
            blueTeam = teams.insert(NewTeam("Blue".toTeamName())) ?: throw Exception("Failed to insert blue team.")
            redTeam = teams.insert(NewTeam("Red".toTeamName())) ?: throw Exception("Failed to insert red team.")
            series =
                SeriesRepository(dsl).insert(
                    NewSeries(
                        eventId = event.id,
                        totalGames = 3,
                        participantIds = Pair(blueTeam.id, redTeam.id),
                        eventStage = EventStage.PLAYOFFS,
                    ),
                ) ?: throw Exception("Failed to insert series.")
            val code =
                TournamentCodeRepository(dsl).insert(
                    NewTournamentCode(seriesId = series.id, blueTeamId = blueTeam.id, redTeamId = redTeam.id),
                    shortcode = "STATS-VIEWS".toShortcode(),
                ) ?: throw Exception("Failed to issue code.")
            val player =
                PlayerRepository(dsl).insert(NewPlayer("Blue Mid".toPlayerName()))
                    ?: throw Exception("Failed to insert player.")
            bluePlayerId = player.id.value
            AccountRepository(dsl).insert(NewAccount(Puuid(BLUE_PUUID), player.id))

            val games = GameRepository(dsl)
            val matchData = RiotMatchDataRepository(dsl)
            withNewObjectives =
                games.insert(NewGame(series.id, code.id, "NA1_1".toRiotMatchId(), null))
                    ?: throw Exception("Failed to insert game.")
            matchData.save(
                withNewObjectives.id,
                match(""", "horde": { "first": true, "kills": 4 }, "atakhan": { "first": true, "kills": 1 }"""),
                null,
            )
            withoutNewObjectives =
                games.insert(NewGame(series.id, code.id, "NA1_2".toRiotMatchId(), null))
                    ?: throw Exception("Failed to insert game.")
            matchData.save(withoutNewObjectives.id, match("", """"someFieldRiotAddsLater": [1, 2],"""), null)
            // The code seats the Blue team on blue, but the PUUID-resolved result says Red won on the blue side.
            swappedSides =
                games.insert(NewGame(series.id, code.id, "NA1_3".toRiotMatchId(), GameResult(redTeam.id, blueTeam.id)))
                    ?: throw Exception("Failed to insert game.")
            matchData.save(swappedSides.id, match(""), null)
        }

        fun playerRows(game: Game) =
            dsl.selectFrom(PLAYER_GAME_STATS_V1).where(PLAYER_GAME_STATS_V1.GAME_ID.eq(game.id.value)).fetch()

        fun teamRows(game: Game) =
            dsl.selectFrom(TEAM_GAME_STATS_V1).where(TEAM_GAME_STATS_V1.GAME_ID.eq(game.id.value)).fetch()

        test("payloads with and without atakhan and horde both produce rows") {
            playerRows(withNewObjectives) shouldHaveSize 2
            teamRows(withNewObjectives) shouldHaveSize 2
            playerRows(withoutNewObjectives) shouldHaveSize 2
            teamRows(withoutNewObjectives) shouldHaveSize 2

            val blueWith = teamRows(withNewObjectives).single { it.side == "BLUE" }
            blueWith.hordeKills shouldBe 4
            val blueWithout = teamRows(withoutNewObjectives).single { it.side == "BLUE" }
            blueWithout.hordeKills shouldBe 0
        }

        test("player rows join to the right team, player, stage and event group") {
            val rows = playerRows(withNewObjectives)
            val blue = rows.single { it.puuid == BLUE_PUUID }
            val red = rows.single { it.puuid == RED_PUUID }

            blue.teamId shouldBe blueTeam.id.value
            blue.playerId shouldBe bluePlayerId
            red.teamId shouldBe redTeam.id.value
            red.playerId.shouldBeNull()
            rows.forEach {
                it.seriesId shouldBe series.id.value
                it.eventId shouldBe series.eventId.value
                it.eventGroupId shouldBe groupId
                it.stage shouldBe EventStage.PLAYOFFS.name
            }
        }

        test("player and team numbers read from the payload, with missing keys as 0") {
            val blue = playerRows(withNewObjectives).single { it.puuid == BLUE_PUUID }
            blue.kills shouldBe 5
            blue.cs shouldBe 230
            blue.position shouldBe "MIDDLE"
            blue.win shouldBe true
            blue.gameDurationS shouldBe 1800
            blue.voidGrubs shouldBe 3
            val red = playerRows(withNewObjectives).single { it.puuid == RED_PUUID }
            red.cs shouldBe 0
            red.voidGrubs shouldBe 0

            val teams = teamRows(withNewObjectives)
            val blueTeamRow = teams.single { it.side == "BLUE" }
            blueTeamRow.teamId shouldBe blueTeam.id.value
            blueTeamRow.firstBlood shouldBe true
            blueTeamRow.towerKills shouldBe 9
            blueTeamRow.bans?.toList() shouldBe listOf(55, 84)
            val redTeamRow = teams.single { it.side == "RED" }
            redTeamRow.teamId shouldBe redTeam.id.value
            redTeamRow.towerKills shouldBe 0
            redTeamRow.bans?.toList() shouldBe emptyList()
        }

        test("teams come from the game result, not the code's sides, when a game has one") {
            playerRows(swappedSides).single { it.puuid == BLUE_PUUID }.teamId shouldBe redTeam.id.value
            playerRows(swappedSides).single { it.puuid == RED_PUUID }.teamId shouldBe blueTeam.id.value
            teamRows(swappedSides).single { it.side == "BLUE" }.teamId shouldBe redTeam.id.value
            teamRows(swappedSides).single { it.side == "RED" }.teamId shouldBe blueTeam.id.value
        }

        test("no view reads teams, so dropping teams.event_id cannot break them") {
            dsl.fetchValue(
                """
                SELECT count(*) FROM information_schema.view_table_usage
                WHERE view_schema = 'dennys' AND table_name = 'teams'
                  AND view_name IN ('player_game_stats_v1', 'team_game_stats_v1')
                """.trimIndent(),
            ) shouldBe 0L
        }
    })
