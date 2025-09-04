import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.models.riot.tournament.toShortcode
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.toTeamName
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class GameRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(
            MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/"
        )
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dsl = DSL.using(ds, SQLDialect.POSTGRES)
    val repo = GameRepository(dsl)
    lateinit var event: Event
    lateinit var team1: Team
    lateinit var team2: Team
    lateinit var series: Series

    beforeSpec {
        val e = EventRepository(dsl)
        event = e.insert(
            NewEvent(
                name = "Test",
                description = "Testing series.",
                startDate = Instant.now(),
                endDate = Instant.now().plusSeconds(3_600L),
                status = EventStatus.ACTIVE
            ), riotTournamentId = 1.toRiotTournamentId()
        ) ?: throw Exception("Failed to insert initial event.")
        val t = TeamRepository(dsl)
        team1 = t.insert(
            NewTeam(
                name = "Team 1".toTeamName(),
            )
        ) ?: throw Exception("Failed to insert team 1.")
        team2 = t.insert(
            NewTeam(
                name = "Team 2".toTeamName(),
            )
        ) ?: throw Exception("Failed to insert team 2.")
        val newSeries = NewSeries(
            eventId = event.id, gamesToWin = 10, participantIds = listOf(team1.id, team2.id)
        )
        val seriesRepository = SeriesRepository(dsl)
        series = seriesRepository.insert(newSeries) ?: throw Exception("Failed to insert initial series.")
    }

    test("Inserting game succeeds and number = 1") {
        val newGame = NewGame(
            blueTeamId = team1.id, redTeamId = team2.id
        )
        val shortcode = "ABCDEFG".toShortcode()
        val game = repo.insert(
            newGame, shortcode = shortcode, seriesId = series.id
        )
        game.shouldNotBeNull()
        game.number shouldBe 1
        game.shortcode shouldBe shortcode
    }
    test("Inserting game succeeds and number = 2") {
        val newGame = NewGame(
            blueTeamId = team1.id, redTeamId = team2.id
        )
        val shortcode = "ABCD".toShortcode()
        val game = repo.insert(
            newGame, shortcode = shortcode, seriesId = series.id
        )
        game.shouldNotBeNull()
        game.number shouldBe 2
        game.shortcode shouldBe shortcode
    }
})
