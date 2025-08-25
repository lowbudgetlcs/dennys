import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.toTeamName
import com.lowbudgetlcs.repositories.EventRepository
import com.lowbudgetlcs.repositories.SeriesRepository
import com.lowbudgetlcs.repositories.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.NoSystemErrListener.prepareSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant

class SeriesRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(
            MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/"
        )
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dsl = DSL.using(ds, SQLDialect.POSTGRES)
    val repo = SeriesRepository(dsl)
    lateinit var event: Event
    lateinit var team1: Team
    lateinit var team2: Team

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
    }

    test("insert and fetch series by id") {
        val newSeries = NewSeries(
            eventId = event.id, gamesToWin = 10, participantIds = listOf(team1.id, team2.id)
        )

        val created = repo.insert(newSeries)
        created.shouldNotBeNull()
        created.gamesToWin shouldBe newSeries.gamesToWin

        val fetched = repo.getById(created.id)
        fetched shouldBe created
    }

    test("series from event exists, then is deleted") {
        var series = repo.getAllByEventId(event.id)
        series.shouldNotBeEmpty()
        series.shouldHaveSize(1)
        repo.delete(series.first().id)
        series = repo.getAllByEventId(event.id)
        series.shouldHaveSize(0)
    }
})
