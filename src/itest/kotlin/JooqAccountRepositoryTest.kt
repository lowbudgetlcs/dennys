import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.repositories.jooq.AccountRepository
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class JooqAccountRepositoryTest : FunSpec({
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
    }
    val ds = install(JdbcDatabaseContainerExtension(postgres))
    val dsl = DSL.using(ds, SQLDialect.POSTGRES)
    val repo = AccountRepository(dsl)

    test("insert and retrieve RiotAccount") {
        val puuid = RiotPuuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC791")
        val newAccount = NewRiotAccount(puuid)
        val inserted = repo.insert(newAccount)

        inserted.shouldNotBeNull()
        inserted.riotPuuid shouldBe puuid
        inserted.playerId shouldBe null

        val fetched = repo.getById(inserted.id)
        fetched shouldBe inserted
    }

    test("getAll returns all inserted accounts") {
        val puuid = RiotPuuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC792")
        repo.insert(NewRiotAccount(puuid))
        val all = repo.getAll()
        all.any { it.riotPuuid == puuid } shouldBe true
    }

    test("getAccountByPuuid returns correct account") {
        val puuid = RiotPuuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC793")
        val inserted = repo.insert(NewRiotAccount(puuid))

        inserted.shouldNotBeNull()
        val fetched = repo.getAccountByPuuid(puuid.value)

        fetched.shouldNotBeNull()
        fetched shouldBe inserted
    }
})
