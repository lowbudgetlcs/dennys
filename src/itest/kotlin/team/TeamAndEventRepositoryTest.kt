package team

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.core.model.Event
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.types.toEventDescription
import com.lowbudgetlcs.domain.event.core.model.types.toEventName
import com.lowbudgetlcs.domain.event.core.model.toRiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.toTeamName
import com.lowbudgetlcs.domain.event.adapter.out.persistence.SqlEventRepository
import com.lowbudgetlcs.domain.team.adapter.out.persistence.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.time.Instant
import java.time.temporal.ChronoUnit

class TeamAndEventRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerSpecExtension(postgres))
        val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
        val eventRepo = SqlEventRepository(dslContext)
        val teamRepo = TeamRepository(dslContext)
        // Data
        val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
        lateinit var event: Event
        lateinit var team: Team

        beforeSpec {
            event = eventRepo.insert(
                NewEvent(
                    name = "Season 1".toEventName(),
                    description = "The first season".toEventDescription(),
                    startDate = now,
                    endDate = now.plusSeconds(604_800L),
                    status = EventStatus.ACTIVE,
                    eventStages = setOf(EventStage.REGULAR_SEASON),
                ),
                12345.toRiotTournamentId(),
            ) ?: throw Exception("Failed to insert initial event.")

            team =
                teamRepo.insert(NewTeam("Test Team".toTeamName())) ?: throw Exception("Failed to insert initial team.")
        }

        "add team to event" {
            val t = teamRepo.update(team, TeamUpdate(eventId = PatchField.Value(event.id)))
            t.shouldNotBeNull()
            t.id shouldBe team.id
            t.eventId shouldBe event.id
        }
    })
