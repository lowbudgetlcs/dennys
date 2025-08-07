import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotAccountId
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.repositories.inmemory.InMemoryAccountRepository
import com.lowbudgetlcs.routes.dto.riot.account.AccountDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class AccountServiceTest : StringSpec({

    val repo = InMemoryAccountRepository()
    val gateway = mockk<IRiotAccountGateway>()
    val service = AccountService(repo, gateway)

    val validPuuid = "a".repeat(78)
    val duplicatePuuid = "b".repeat(78)
    val badRequestPuuid = "c".repeat(78)
    val notFoundPuuid = "d".repeat(78)
    val apiErrorPuuid = "e".repeat(78)

    beforeTest {
        repo.clear()
    }

    "createAccount should succeed for a new valid Riot account" {
        val newAccount = NewRiotAccount(RiotPuuid(validPuuid))
        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        val created = service.createAccount(newAccount)

        created.riotPuuid.value shouldBe validPuuid
        repo.getAll().shouldContainExactly(created)
    }

    "createAccount should throw if puuid is already taken" {
        val puuid = RiotPuuid(duplicatePuuid)
        val newAccount = NewRiotAccount(puuid)

        coEvery { gateway.getAccountByPuuid(duplicatePuuid) } returns AccountDto(puuid = duplicatePuuid)
        service.createAccount(newAccount)

        val exception = shouldThrow<IllegalStateException> {
            service.createAccount(newAccount)
        }
        exception.message shouldBe "Riot account already exists"
    }

    "createAccount should throw IllegalArgumentException for malformed PUUID (400)" {
        val puuid = RiotPuuid(badRequestPuuid)
        val newAccount = NewRiotAccount(puuid)

        coEvery { gateway.getAccountByPuuid(badRequestPuuid) } throws IllegalArgumentException("Invalid Riot PUUID")

        val exception = shouldThrow<IllegalArgumentException> {
            service.createAccount(newAccount)
        }
        exception.message shouldBe "Invalid Riot PUUID"
    }

    "createAccount should throw NoSuchElementException for non-existent account (404)" {
        val puuid = RiotPuuid(notFoundPuuid)
        val newAccount = NewRiotAccount(puuid)

        coEvery { gateway.getAccountByPuuid(notFoundPuuid) } throws NoSuchElementException("Riot account not found for PUUID")

        val exception = shouldThrow<NoSuchElementException> {
            service.createAccount(newAccount)
        }
        exception.message shouldBe "Riot account not found for PUUID"
    }

    "createAccount should throw RiotApiException for unexpected Riot API failure" {
        val puuid = RiotPuuid(apiErrorPuuid)
        val newAccount = NewRiotAccount(puuid)

        coEvery { gateway.getAccountByPuuid(apiErrorPuuid) } throws RiotApiException("Unexpected Riot API error: 500 Internal Server Error")

        val exception = shouldThrow<RiotApiException> {
            service.createAccount(newAccount)
        }
        exception.message shouldBe "Unexpected Riot API error: 500 Internal Server Error"
    }

    "getAccount should return a stored Riot account" {
        val puuid = RiotPuuid(validPuuid)
        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        val created = service.createAccount(NewRiotAccount(puuid))
        val found = service.getAccount(created.id)

        found shouldBe created
    }

    "getAccount should throw if ID not found" {
        val exception = shouldThrow<NoSuchElementException> {
            service.getAccount(RiotAccountId(999))
        }
        exception.message shouldBe "Account not found"
    }

    "getAllAccounts should return all stored accounts" {
        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        val created = service.createAccount(NewRiotAccount(RiotPuuid(validPuuid)))
        val all = service.getAllAccounts()

        all shouldContainExactly listOf(created)
    }

    "isPuuidTaken should reflect correct state" {
        val puuid = RiotPuuid(validPuuid)

        service.isPuuidTaken(puuid) shouldBe false

        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)
        service.createAccount(NewRiotAccount(puuid))

        service.isPuuidTaken(puuid) shouldBe true
    }
})