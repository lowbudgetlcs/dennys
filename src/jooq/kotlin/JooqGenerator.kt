import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.util.*

class JooqGenerator : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }

    val ds = install(JdbcDatabaseContainerExtension(postgres))

    @Suppress("UNCHECKED_CAST")
    fun getProp(key: String): String {
        val props = this::class.java.getResourceAsStream("codegen.properties").use {
            Properties().apply { load(it) }
        }
        return props.getProperty(key) ?: ""
    }

    test("Generate jooq POJOs") {
        val config = Configuration().apply {
            jdbc = Jdbc().apply {
                driver = "org.postgresql.Driver"
                url = ds.jdbcUrl
                user = ds.username
                password = ds.password
            }
            generator = Generator().apply {
                name = "org.jooq.codegen.KotlinGenerator"
                database = Database().apply {
                    name = "org.jooq.meta.postgres.PostgresDatabase"
                    includes = ".*"
                    forcedTypes = listOf(ForcedType().apply {
                        name = "INSTANT"
                        includeTypes = "TIMESTAMPTZ"
                    })
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
})