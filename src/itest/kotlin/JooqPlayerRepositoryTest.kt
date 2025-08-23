import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.repositories.PlayerRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class JooqPlayerRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dsl = DSL.using(ds, SQLDialect.POSTGRES)
    val repo = PlayerRepository(dsl)

    test("insert and fetch player by id") {
        val newPlayer = NewPlayer(PlayerName("TestPlayer#123"))
        val created = repo.insert(newPlayer)

        created.shouldNotBeNull()
        created.name shouldBe newPlayer.name

        val fetched = repo.getById(created.id)
        fetched shouldBe created
    }

    test("rename player updates name") {
        val newPlayer = NewPlayer(PlayerName("OldName#XYZ"))
        val created = repo.insert(newPlayer)!!
        val renamed = repo.renamePlayer(created.id, PlayerName("NewName#XYZ"))

        renamed.shouldNotBeNull()
        renamed.id shouldBe created.id
        renamed.name.value shouldBe "NewName#XYZ"
    }
})
