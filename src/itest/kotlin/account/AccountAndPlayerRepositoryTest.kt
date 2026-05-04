package account

import com.lowbudgetlcs.domain.account.adapter.out.persistence.SqlAccountRepository
import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.domain.player.adapter.out.persistence.PlayerRepository
import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.toPlayerId
import com.lowbudgetlcs.domain.player.core.model.types.toPlayerName
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class AccountAndPlayerRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds = install(JdbcDatabaseContainerSpecExtension(postgres))
        val dsl = DSL.using(ds, SQLDialect.POSTGRES)
        val accountRepo = SqlAccountRepository(dsl)
        val playerRepo = PlayerRepository(dsl)
        lateinit var account: Account
        lateinit var player: Player
        lateinit var player2: Player

        beforeSpec {
            player = playerRepo.insert(
                NewPlayer(
                    name = "ruuffian".toPlayerName(),
                ),
            ) ?: throw Exception("Failed to insert initial player.")
            player2 = playerRepo.insert(
                NewPlayer(
                    name = "zain".toPlayerName(),
                ),
            ) ?: throw Exception("Failed to insert initial player2.")
            account =
                accountRepo.insert(
                    NewAccount(
                        puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC794"),
                    ),
                )
                    ?: throw Exception("Failed to insert initial account.")
        }

        "Updating an account with a valid playerId succeeds." {
            val a = accountRepo.updatePlayerId(account.id, player.id)
            a.shouldNotBeNull()
            a.shouldBeEqualToIgnoringFields(account, Account::playerId)
            a.playerId.shouldNotBeNull()
            a.playerId shouldBe player.id
        }

        "Updating an account with an invalid playerId fails." {
            shouldThrow<IntegrityConstraintViolationException> {
                accountRepo.updatePlayerId(account.id, (-1).toPlayerId())
            }
        }

        "Inserting an account with a valid playerId succeeds." {
            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC791")
            val a =
                accountRepo.insert(
                    NewAccount(
                        puuid = puuid,
                        playerId = player2.id,
                    ),
                )
            a.shouldNotBeNull()
            a.playerId.shouldNotBeNull()
            a.playerId shouldBe player2.id
        }

        "Inserting an account with an invalid playerId fails." {
            val puuid = Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC792")
            val id = PlayerId(-1)
            shouldThrow<IntegrityConstraintViolationException> {
                accountRepo.insert(NewAccount(puuid, id))
            }
        }
    })
