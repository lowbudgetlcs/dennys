import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotAccountId
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.repositories.inmemory.InMemoryAccountRepository
import com.lowbudgetlcs.routes.dto.riot.account.AccountDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class AccountServiceTest : StringSpec({

    val repo = InMemoryAccountRepository()
    val gateway = mockk<IRiotAccountGateway>()
    val service = AccountService(repo, gateway)

    val validPuuid = "a".repeat(78)
    val invalidPuuid = "b".repeat(78)

    beforeTest {
        repo.clear()
    }

    "createAccount should succeed for valid and new puuid" {
        val newAccount = NewRiotAccount(RiotPuuid(validPuuid))

        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        val created = service.createAccount(newAccount)

        created.shouldNotBeNull()
        created.riotPuuid.value shouldBe validPuuid
        created.playerId.shouldBeNull()

        service.getAllAccounts().map { it.id } shouldContainExactly listOf(created.id)
    }

    "createAccount should fail if puuid already exists" {
        val puuid = RiotPuuid(validPuuid)

        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        service.createAccount(NewRiotAccount(puuid))!!.shouldNotBeNull()

        val exception = shouldThrow<IllegalStateException> {
            service.createAccount(NewRiotAccount(puuid))
        }

        exception.message shouldBe "Riot account already exists"
    }

    "createAccount should fail if puuid not found by Riot API" {
        val puuid = RiotPuuid(invalidPuuid)

        coEvery { gateway.getAccountByPuuid(invalidPuuid) } returns null

        val exception = shouldThrow<NoSuchElementException> {
            service.createAccount(NewRiotAccount(puuid))
        }

        exception.message shouldBe "Riot account not found from Riot API"
    }

    "getAccount should return correct record" {
        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        val created = service.createAccount(NewRiotAccount(RiotPuuid(validPuuid)))!!
        val fetched = service.getAccount(created.id)

        fetched shouldBe created
    }

    "getAccount should return null for nonexistent ID" {
        val result = service.getAccount(RiotAccountId(999))
        result.shouldBeNull()
    }

    "getAllAccounts should return all inserted accounts" {
        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)

        val created = service.createAccount(NewRiotAccount(RiotPuuid(validPuuid)))!!
        val all = service.getAllAccounts()

        all.shouldContainExactly(created)
    }

    "isPuuidTaken should return correct boolean" {
        val puuid = RiotPuuid(validPuuid)

        service.isPuuidTaken(puuid) shouldBe false

        coEvery { gateway.getAccountByPuuid(validPuuid) } returns AccountDto(puuid = validPuuid)
        service.createAccount(NewRiotAccount(puuid))!!

        service.isPuuidTaken(puuid) shouldBe true
    }
})