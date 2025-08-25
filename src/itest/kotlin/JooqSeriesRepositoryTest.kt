import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.TeamId
import com.lowbudgetlcs.domain.models.events.EventId
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class JooqSeriesRepositoryTest :
        FunSpec({
            val postgres =
                    PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                        withCopyFileToContainer(
                                MountableFile.forClasspathResource("sql"),
                                "/docker-entrypoint-initdb.d/"
                        )
                    }
            val ds = install(JdbcDatabaseContainerExtension(postgres))
            val dsl = DSL.using(ds, SQLDialect.POSTGRES)
            val repo = SeriesRepository(dsl)

            test("insert and fetch team by id") {
                val newSeries =
                        NewSeries(
                                eventId = EventId("123"),
                                gamesToWin = 10,
                                participantIds = listOf(TeamId("1"), TeamId("2"))
                        )

                val created = repo.insert(newSeries)
                created.shouldNotBeNull()
                created.gamesToWin shouldBe newTeam.gamesToWin

                val fetched = repo.getById(created.id)
                fetched shouldBe created
            }
        })
