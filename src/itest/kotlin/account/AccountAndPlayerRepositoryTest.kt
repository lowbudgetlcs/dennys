package account

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.repositories.account.AccountRepository
import com.lowbudgetlcs.repositories.player.PlayerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class AccountAndPlayerRepositoryTest :
    FunSpec({
        val postgres =
            PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val accountRepo = AccountRepository(dsl)
        val playerRepo = PlayerRepository(dsl)

        test("Inserting an account with a valid playerId succeeds.") {
            val p =
                playerRepo.insert(
                    NewPlayer(
                        name = "ruuffian".toPlayerName(),
                    ),
                )
            p.shouldNotBeNull()

            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC791")
            val a =
                accountRepo.insert(
                    NewAccount(
                        puuid = puuid,
                        playerId = p.id,
                    ),
                )
            a.shouldNotBeNull()
            a.playerId.shouldNotBeNull()
            a.playerId shouldBe p.id
        }

        test("Inserting an account with an invalid playerId fails.") {
            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC792")
            val id = PlayerId(-1)
            shouldThrow<IntegrityConstraintViolationException> {
                accountRepo.insert(NewAccount(puuid, id))
            }
        }

        test("Updating an account with a valid playerId succeeds.") {
            val p = playerRepo.insert(NewPlayer("zain".toPlayerName()))
            p.shouldNotBeNull()
            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC793")
            val a = accountRepo.insert(NewAccount(puuid))

            a.shouldNotBeNull()

            val b = accountRepo.updatePlayerId(a.id, p.id)
            b.shouldNotBeNull()
            b.shouldBeEqualToIgnoringFields(a, Account::playerId)
            b.playerId.shouldNotBeNull()
            b.playerId shouldBe p.id
        }
    })
