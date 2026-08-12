package services

import com.lowbudgetlcs.api.dto.players.toDto
import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.player.PlayerService
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

private val FULL_METAL = 3.toPlayerId()
private val SEIR = 2.toPlayerId()

/** Distinct 78-character base64url PUUIDs; only the last character differs. */
private fun puuidOf(suffix: Char) =
    Puuid("mCLCPW2XhEy2NpOk3yoDHWPN-Fu-tWnZ-klQ1lBMNgH38k-0JTN27aBh0xT9_F2aD4SvkLj1CpC79$suffix")

private fun accountOf(
    id: Int,
    owner: PlayerId?,
    suffix: Char,
) = Account(AccountId(id), puuidOf(suffix), owner)

class PlayerWithAccountsTest :
    StringSpec({
        val playerRepo = mockk<IPlayerRepository>()
        val accountRepo = mockk<IAccountRepository>()
        val teamRepo = mockk<ITeamRepository>()
        val service = PlayerService(playerRepo, accountRepo, teamRepo)

        beforeEach { clearMocks(playerRepo, accountRepo, teamRepo) }

        val fullMetal = Player(FULL_METAL, "Full Metal".toPlayerName())
        val seir = Player(SEIR, "seir".toPlayerName())

        "a player's linked accounts come back on the player" {
            val linked = listOf(accountOf(1, FULL_METAL, '1'), accountOf(2, FULL_METAL, '2'))
            every { playerRepo.getById(FULL_METAL) } returns fullMetal
            every { accountRepo.getByPlayerId(FULL_METAL) } returns linked

            service.getPlayerWithAccounts(FULL_METAL).accounts shouldContainExactly linked
        }

        "the account reaches the DTO the API actually returns" {
            every { playerRepo.getById(FULL_METAL) } returns fullMetal
            every { accountRepo.getByPlayerId(FULL_METAL) } returns listOf(accountOf(1, FULL_METAL, '1'))

            val dto = service.getPlayerWithAccounts(FULL_METAL).toDto()

            dto.accounts.map { it.id } shouldContainExactly listOf(1)
            dto.accounts.single().riotPuuid shouldBe puuidOf('1').value
            dto.accounts.single().playerId shouldBe FULL_METAL.value
        }

        "a player with no accounts reports an empty list rather than failing" {
            every { playerRepo.getById(FULL_METAL) } returns fullMetal
            every { accountRepo.getByPlayerId(FULL_METAL) } returns emptyList()

            service.getPlayerWithAccounts(FULL_METAL).accounts.shouldBeEmpty()
        }

        "an unknown player is still not found" {
            every { playerRepo.getById(FULL_METAL) } returns null

            shouldThrow<NoSuchElementException> { service.getPlayerWithAccounts(FULL_METAL) }
        }

        "the list endpoint gives each player only their own accounts" {
            every { playerRepo.getAll() } returns listOf(fullMetal, seir)
            every { accountRepo.getAll() } returns
                listOf(
                    accountOf(1, FULL_METAL, '1'),
                    accountOf(2, SEIR, '2'),
                    accountOf(3, FULL_METAL, '4'),
                )

            val byName = service.getAllPlayersWithAccounts().associateBy { it.name.value }

            byName.getValue("Full Metal").accounts.map { it.id.value } shouldContainExactly listOf(1, 3)
            byName.getValue("seir").accounts.map { it.id.value } shouldContainExactly listOf(2)
        }

        "an unlinked account is attached to nobody" {
            every { playerRepo.getAll() } returns listOf(fullMetal, seir)
            every { accountRepo.getAll() } returns listOf(accountOf(9, null, '1'))

            service.getAllPlayersWithAccounts().forEach { it.accounts.shouldBeEmpty() }
        }

        "a player with no accounts still appears in the list" {
            every { playerRepo.getAll() } returns listOf(fullMetal, seir)
            every { accountRepo.getAll() } returns listOf(accountOf(1, FULL_METAL, '1'))

            val all = service.getAllPlayersWithAccounts()

            all.map { it.id } shouldContainExactly listOf(FULL_METAL, SEIR)
            all.single { it.id == SEIR }.accounts.shouldBeEmpty()
        }

        "accounts are fetched once for the whole list, not once per player" {
            every { playerRepo.getAll() } returns listOf(fullMetal, seir)
            every { accountRepo.getAll() } returns listOf(accountOf(1, FULL_METAL, '1'))

            service.getAllPlayersWithAccounts()

            verify(exactly = 1) { accountRepo.getAll() }
            verify(exactly = 0) { accountRepo.getByPlayerId(any()) }
        }

        "the teams view carries accounts as well as teams" {
            every { playerRepo.getById(FULL_METAL) } returns fullMetal
            every { teamRepo.getByPlayerId(FULL_METAL) } returns emptyList()
            every { accountRepo.getByPlayerId(FULL_METAL) } returns listOf(accountOf(1, FULL_METAL, '1'))

            val withTeams = service.getPlayerWithTeams(FULL_METAL)

            withTeams.accounts.map { it.id.value } shouldContainExactly listOf(1)
            withTeams.toDto().accounts.map { it.id } shouldContainExactly listOf(1)
        }
    })
