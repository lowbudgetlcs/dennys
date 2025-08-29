import com.lowbudgetlcs.repositories.metadata.MetadataRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class MetadataRepositoryTest : StringSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
    val repo = MetadataRepository(dslContext)

    "getProviderId() succeeds and returns correct provider ID" {
        val id = repo.getProviderId()
        id shouldBe 1
    }

    "setProviderId() succeeds and returns ID" {
        val id = repo.setProviderId(3)
        id shouldBe 3
        val new = repo.getProviderId()
        new shouldBe 3
    }

})
