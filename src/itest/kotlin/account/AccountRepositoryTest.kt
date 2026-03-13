package account

import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.repositories.account.AccountRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class AccountRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerSpecExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = AccountRepository(dsl)
        val testPuuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC791")

        "getAll returns an empty list" {
            repo.getAll().shouldHaveSize(0)
        }

        "getById returns null for nonexistant account." {
            repo.getById(AccountId(-1)).shouldBeNull()
        }

        "insert successfully creates an account with a null playerId. This account can be retrieved by getById" {
            val newAccount = NewAccount(testPuuid)
            val inserted = repo.insert(newAccount)

            inserted.shouldNotBeNull()
            inserted.puuid shouldBe testPuuid
            inserted.playerId shouldBe null

            val fetched = repo.getById(inserted.id)
            fetched shouldBe inserted
        }

        "insert fails when duplicate puuid provided." {
            val newAccount = NewAccount(testPuuid)
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newAccount)
            }
        }

        "getAll returns all inserted accounts" {
            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC792")
            repo.insert(NewAccount(puuid))
            val all = repo.getAll()
            all.shouldHaveSize(2)
            all.any { it.puuid == puuid } shouldBe true
        }

        "getAccountByPuuid returns correct account" {
            val fetched = repo.getAccountByPuuid(testPuuid)

            fetched.shouldNotBeNull()
            fetched.puuid shouldBe testPuuid
        }
    })
