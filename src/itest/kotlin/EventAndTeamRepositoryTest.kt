import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.events.EventStatus
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.toEvent
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.team.toTeam
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.team.toTeamName
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.repositories.EventRepository
import com.lowbudgetlcs.repositories.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventAndTeamRepositoryTest : StringSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
    val eventRepo = EventRepository(dslContext)
    val teamRepo = TeamRepository(dslContext)
    // Data
    val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
    val newEvent = NewEvent(
        name = "Season 1",
        description = "The first season",
        startDate = now,
        endDate = now.plusSeconds(604_800L),
        status = EventStatus.ACTIVE
    )
    val expectedEvent = newEvent.toEvent(
        id = 1.toEventId(), createdAt = Instant.now(), tournamentId = 1245.toTournamentId()
    )
    val newTeam = NewTeam(
        name = "Test Team".toTeamName(),
    )
    val expectedTeam = newTeam.toTeam(
        0.toTeamId(), eventId = null
    )

    fun checkEvent(event: Event) {
        event.shouldBeEqualToIgnoringFields(expectedEvent, Event::id, Event::createdAt, Event::tournamentId)
    }

    fun checkTeam(team: Team) {
        team.shouldBeEqualToIgnoringFields(expectedTeam, Team::id, Team::eventId)
    }

    "insert a new event" {
        val event = eventRepo.insert(newEvent, 1245.toTournamentId())
        event.shouldNotBeNull()
        checkEvent(event)
    }

    "insert a new team" {
        val team = teamRepo.insert(newTeam)
        team.shouldNotBeNull()
        checkTeam(team)
        team.eventId shouldBe null
    }

    "add team to event" {
        val events = eventRepo.getAll()
        events.shouldNotBeEmpty()
        val event = events.first()
        val teams = teamRepo.getAll()
        teams.shouldNotBeEmpty()
        val team = teams.first()
        val t = teamRepo.updateEventId(team.id, event.id)
        t.shouldNotBeNull()
        checkTeam(t)
        t.eventId shouldBe event.id
    }
})