import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.TeamLogoName
import com.lowbudgetlcs.domain.models.team.TeamName
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

class TeamRepositoryTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = TeamRepository(dsl)

        test("insert and fetch team by id") {
            val newTeam =
                NewTeam(
                    name = TeamName("Golden Guardians"),
                    logoName = null,
                )

            val created = repo.insert(newTeam)
            created.shouldNotBeNull()
            created.name shouldBe newTeam.name
            created.logoName shouldBe null

            val fetched = repo.getById(created.id)
            fetched shouldBe created
        }

        test("update team name") {
            val created = repo.insert(NewTeam(TeamName("Old Name"), null))!!
            val updated = repo.updateTeamName(created.id, TeamName("New Name"))

            updated.shouldNotBeNull()
            updated.id shouldBe created.id
            updated.name.value shouldBe "New Name"
        }

        test("update team logo") {
            val created = repo.insert(NewTeam(TeamName("Logo Team"), null))!!
            val updated = repo.updateTeamLogoName(created.id, TeamLogoName("ggs.png"))

            updated.shouldNotBeNull()
            updated.logoName?.value shouldBe "ggs.png"
        }
    })
