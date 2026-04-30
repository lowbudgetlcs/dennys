package team

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.toTeamName
import com.lowbudgetlcs.domain.team.adapter.out.persistence.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class TeamRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds =
            install(JdbcDatabaseContainerSpecExtension(postgres)) {
                maximumPoolSize = 1
            }
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = TeamRepository(dsl)

        val newTeam =
            NewTeam(
                name = "Golden Guardians".toTeamName(),
                null,
            )

        "getAll returns 0 teams" {
            val teams = repo.getAll()
            teams.shouldHaveSize(0)
        }

        "insert and fetch team by id" {
            val created = repo.insert(newTeam)
            created.shouldNotBeNull()
            created.name shouldBe newTeam.name

            val fetched = repo.getById(created.id)
            fetched shouldBe created
        }

        "update team name" {
            val created = repo.insert(NewTeam("Old Name".toTeamName()))!!
            val updated = repo.update(created, TeamUpdate(name = "New Name".toTeamName()))

            updated.shouldNotBeNull()
            updated.id shouldBe created.id
            updated.name.value shouldBe "New Name"
        }

        "update logo" {
            val created = repo.insert(NewTeam("Testing".toTeamName()))!!
            val updated = repo.update(created, TeamUpdate(logo = PatchField.Value("New Name")))

            updated.shouldNotBeNull()
            updated.id shouldBe created.id
            updated.logo shouldBe "New Name"
        }

        "getAll returns 3 teams" {
            val teams = repo.getAll()
            teams.shouldHaveSize(3)
        }
    })
