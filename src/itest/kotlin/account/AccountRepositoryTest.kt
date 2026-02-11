package account

import com.lowbudgetlcs.domain.models.player.account.AccountId
import com.lowbudgetlcs.domain.models.player.account.NewAccount
import com.lowbudgetlcs.domain.models.player.account.Puuid
import com.lowbudgetlcs.repositories.account.AccountRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class AccountRepositoryTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = AccountRepository(dsl)
        val testPuuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC791")

        test("getAll returns an empty list") {
            repo.getAll().shouldHaveSize(0)
        }

        test("getById returns null for nonexistant account.") {
            repo.getById(AccountId(-1)).shouldBeNull()
        }

        test("insert successfully creates an account with a null playerId. This account can be retrieved by getById") {
            val newAccount = NewAccount(testPuuid)
            val inserted = repo.insert(newAccount)

            inserted.shouldNotBeNull()
            inserted.puuid shouldBe testPuuid
            inserted.playerId shouldBe null

            val fetched = repo.getById(inserted.id)
            fetched shouldBe inserted
        }

        test("insert fails when duplicate puuid provided.") {
            val newAccount = NewAccount(testPuuid)
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newAccount)
            }
        }

        test("getAll returns all inserted accounts") {
            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC792")
            repo.insert(NewAccount(puuid))
            val all = repo.getAll()
            all.shouldHaveSize(2)
            all.any { it.puuid == puuid } shouldBe true
        }

        test("getAccountByPuuid returns correct account") {
            val fetched = repo.getAccountByPuuid(testPuuid)

            fetched.shouldNotBeNull()
            fetched.puuid shouldBe testPuuid
        }
    })
