import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.util.*

@Testcontainers
class JooqGenerator {

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:15-alpine").apply {
            withCopyFileToContainer(MountableFile.forClasspathResource("migrations/"), "/docker-entrypoint-initdb.d/")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getProp(key: String): String {
        val props = this::class.java.getResourceAsStream("codegen.properties").use {
            Properties().apply { load(it) }
        }
        return props.getProperty(key) ?: ""
    }

    @Test
    fun generate() {
        val config = Configuration().apply {
            jdbc = Jdbc().apply {
                driver = "org.postgresql.Driver"
                url = postgres.jdbcUrl
                user = postgres.username
                password = postgres.password
            }
            generator = Generator().apply {
                name = "org.jooq.codegen.KotlinGenerator"
                database = Database().apply {
                    name = "org.jooq.meta.postgres.PostgresDatabase"
                    includes = ".*"
                    inputSchema = "dennys"
                }
                target = Target().apply {
                    packageName = getProp("jooq.packageName")
                    directory = getProp("jooq.directory")
                }
            }
        }
        GenerationTool.generate(config)
    }
}