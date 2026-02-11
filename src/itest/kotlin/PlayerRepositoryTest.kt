import com.lowbudgetlcs.domain.models.player.NewPlayer
import com.lowbudgetlcs.domain.models.player.toPlayerId
import com.lowbudgetlcs.domain.models.player.toPlayerName
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.repositories.player.PlayerRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class PlayerRepositoryTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = PlayerRepository(dsl)

        test("getAll returns empty list.") {
            repo.getAll().shouldHaveSize(0)
        }

        test("getById returns null.") {
            repo.getById((-1).toPlayerId()) shouldBe null
        }

        test("getByTeamId returns empty list.") {
            repo.getByTeamId((-1).toTeamId()).shouldHaveSize(0)
        }

        test("insert succeeds and getById fetches the same player.") {
            val newPlayer = NewPlayer("ruuffian".toPlayerName())
            val created = repo.insert(newPlayer)

            created.shouldNotBeNull()
            created.name shouldBe newPlayer.name

            val fetched = repo.getById(created.id)
            fetched shouldBe created
        }

        test("rename player updates name") {
            val newPlayer = NewPlayer("zain".toPlayerName())
            val created = repo.insert(newPlayer)
            created.shouldNotBeNull()
            val renamed = repo.renamePlayer(created.id, "new Zain".toPlayerName())

            renamed.shouldNotBeNull()
            renamed.id shouldBe created.id
            renamed.name shouldNotBe newPlayer.name
            renamed.name shouldBe "new Zain".toPlayerName()
        }
    })
