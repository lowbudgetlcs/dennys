import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import java.util.*

object JooqCodegenRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val props = JooqCodegenRunner::class.java.getResourceAsStream("/codegen.properties").use {
            Properties().apply { load(it) }
        }
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("postgres")
            withUsername(props.getProperty("db.username"))
            withPassword(props.getProperty("db.password"))
            withCopyFileToContainer(
                MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/"
            )
        }
        try {
            postgres.start()
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
                        inputSchema = "dennys"
                        includes = ".*"
                    }
                    target = org.jooq.meta.jaxb.Target().apply {
                        packageName = props.getProperty("jooq.packageName")
                        directory = props.getProperty("jooq.directory")
                    }
                }
            }

            GenerationTool.generate(config)
        } finally {
            postgres.stop()
        }
    }
}