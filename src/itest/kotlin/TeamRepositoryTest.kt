import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.toTeamLogoName
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.domain.team.models.types.TeamName
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class TeamRepositoryTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = TeamRepository(dsl)

        val newTeam =
            NewTeam(
                name = "Golden Guardians".toTeamName(),
                logoName = null,
            )

        test("getAll returns 0 teams") {
            val teams = repo.getAll()
            teams.shouldHaveSize(0)
        }

        test("insert and fetch team by id") {
            val created = repo.insert(newTeam)
            created.shouldNotBeNull()
            created.name shouldBe newTeam.name
            created.logoName shouldBe null

            val fetched = repo.getById(created.id)
            fetched shouldBe created
        }

        test("update team name") {
            val created = repo.insert(NewTeam("Old Name".toTeamName(), null))!!
            val updated = repo.update(created, TeamUpdate(name = "New Name".toTeamName()))

            updated.shouldNotBeNull()
            updated.id shouldBe created.id
            updated.name.value shouldBe "New Name"
        }

        test("update team logo") {
            val created = repo.insert(NewTeam(TeamName("Logo Team"), null))!!
            val updated =
                repo.update(
                    created,
                    TeamUpdate(logoName = "ggs.png".toTeamLogoName()),
                )

            updated.shouldNotBeNull()
            updated.logoName?.value shouldBe "ggs.png"
        }

        test("getAll returns 3 teams") {
            val teams = repo.getAll()
            teams.shouldHaveSize(3)
        }
    })
