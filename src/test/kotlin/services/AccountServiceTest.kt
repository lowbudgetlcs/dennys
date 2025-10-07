package services

import com.lowbudgetlcs.api.dto.riot.account.RiotAccountDto
import com.lowbudgetlcs.domain.models.player.toPlayerId
import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.domain.models.riot.account.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotPuuid
import com.lowbudgetlcs.domain.models.riot.account.toRiotAccount
import com.lowbudgetlcs.domain.models.riot.account.toRiotAccountId
import com.lowbudgetlcs.domain.services.account.AccountService
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.repositories.account.IAccountRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

class AccountServiceTest :
    StringSpec({

        val repo = mockk<IAccountRepository>()
        val gateway = mockk<IRiotAccountGateway>()
        val service = AccountService(repo, gateway)

        val puuid = "a".repeat(78)
        val puuid2 = "c".repeat(78)

        "createAccount should succeed for a new valid Riot account" {
            val newAccount = NewRiotAccount(RiotPuuid(puuid))
            val expectedAccount =
                newAccount.toRiotAccount(
                    1.toRiotAccountId(),
                    1.toPlayerId(),
                )
            every { repo.getAccountByPuuid(puuid) } returns null
            coEvery { gateway.getAccountByPuuid(puuid) } returns RiotAccountDto(puuid = puuid)
            every { repo.insert(newAccount) } returns expectedAccount

            val created = service.createAccount(newAccount)

            created.riotPuuid.value shouldBe puuid
            created shouldBe expectedAccount
        }

        "createAccount should throw if puuid is already taken" {
            val duplicateAccount = NewRiotAccount(RiotPuuid(puuid))
            val expectedAccount = duplicateAccount.toRiotAccount(1.toRiotAccountId(), 1.toPlayerId())
            every { repo.getAccountByPuuid(puuid) } returns expectedAccount

            val exception =
                shouldThrow<IllegalStateException> {
                    service.createAccount(duplicateAccount)
                }
            exception.message shouldBe "Riot account already exists"
        }

        // NOTE: These should probably be put into a RiotAccountGateway test instead of in the service.
        // We are stubbing the gateway, so testing it here is incorrect.
        "createAccount should throw IllegalArgumentException for malformed PUUID (400)" {
            val invalidAccount = NewRiotAccount(RiotPuuid(puuid))

            every { repo.getAccountByPuuid(puuid) } returns null
            coEvery { gateway.getAccountByPuuid(puuid) } throws IllegalArgumentException("Invalid Riot PUUID")

            val exception =
                shouldThrow<IllegalArgumentException> {
                    service.createAccount(invalidAccount)
                }
            exception.message shouldBe "Invalid Riot PUUID"
        }

        "createAccount should throw NoSuchElementException for non-existent account (404)" {
            val nonExistantAccount = NewRiotAccount(RiotPuuid(puuid))

            every { repo.getAccountByPuuid(puuid) } returns null
            coEvery { gateway.getAccountByPuuid(puuid) } throws
                NoSuchElementException("Riot account not found for PUUID")

            val exception =
                shouldThrow<NoSuchElementException> {
                    service.createAccount(nonExistantAccount)
                }
            exception.message shouldBe "Riot account not found for PUUID"
        }

        "createAccount should throw RiotApiException for unexpected Riot API failure" {
            val failedAccount = NewRiotAccount(RiotPuuid(puuid))

            every { repo.getAccountByPuuid(puuid) } returns null
            coEvery { gateway.getAccountByPuuid(puuid) } throws
                RiotApiException("Unexpected Riot API error: 500 Internal Server Error")

            val exception =
                shouldThrow<RiotApiException> {
                    service.createAccount(failedAccount)
                }
            exception.message shouldBe "Unexpected Riot API error: 500 Internal Server Error"
        }

        "getAccount should return a stored Riot account" {
            val newAccount = NewRiotAccount(RiotPuuid(puuid))
            val expectedAccount = newAccount.toRiotAccount(1.toRiotAccountId(), 1.toPlayerId())
            every { repo.getById(expectedAccount.id) } returns expectedAccount

            val found = service.getAccount(expectedAccount.id)

            found shouldBe expectedAccount
        }

        "getAccount should throw if ID not found" {
            val newAccount = NewRiotAccount(RiotPuuid(puuid))
            val expectedAccount = newAccount.toRiotAccount(1.toRiotAccountId(), 1.toPlayerId())
            every { repo.getById(expectedAccount.id) } throws NoSuchElementException("Account not found")

            val exception =
                shouldThrow<NoSuchElementException> {
                    service.getAccount(expectedAccount.id)
                }
            exception.message shouldBe "Account not found"
        }

        "getAllAccounts should return all stored accounts" {
            val accounts =
                listOf(
                    RiotAccount(
                        id = 0.toRiotAccountId(),
                        riotPuuid = RiotPuuid(puuid),
                        playerId = 1.toPlayerId(),
                    ),
                    RiotAccount(
                        id = 1.toRiotAccountId(),
                        riotPuuid = RiotPuuid(puuid2),
                        playerId = 2.toPlayerId(),
                    ),
                )
            every { repo.getAll() } returns accounts

            val all = service.getAllAccounts()

            all shouldContainExactly accounts
        }

        "isPuuidTaken should reflect correct state" {
            val newAccount = NewRiotAccount(RiotPuuid(puuid))
            val expectedAccount = newAccount.toRiotAccount(1.toRiotAccountId(), 1.toPlayerId())
            every { repo.getAccountByPuuid(puuid) } returns null
            service.isPuuidTaken(RiotPuuid(puuid)) shouldBe false

            every { repo.getAccountByPuuid(puuid) } returns expectedAccount
            service.isPuuidTaken(RiotPuuid(puuid)) shouldBe true
        }
    })
