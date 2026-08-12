package services

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.player.PlayerService
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/** The account from the production report: created, unowned, and refused as "already owned". */
private val FULL_METAL_PUUID = Puuid("wtATP4ScufpCsr8o7FaDd2wQu5u3dZjPWLiU-W0dJ41EVO_JuSrDDBV680ygdBARc7H7GARUTtJjeA")
private val ACCOUNT_ID = AccountId(1)
private val OWNER = 3.toPlayerId()
private val SOMEONE_ELSE = 2.toPlayerId()

class PlayerAccountLinkTest :
    StringSpec({
        val playerRepo = mockk<IPlayerRepository>()
        val accountRepo = mockk<IAccountRepository>()
        val teamRepo = mockk<ITeamRepository>()
        val service = PlayerService(playerRepo, accountRepo, teamRepo)

        // Scoped to this spec's own mocks: clearAllMocks() is global and wipes the stubs of
        // whatever spec is running concurrently.
        beforeEach { clearMocks(playerRepo, accountRepo, teamRepo) }

        val fullMetal = Player(OWNER, "Full Metal".toPlayerName())

        /** An account held by [owner], or unowned when null. */
        fun accountOwnedBy(owner: PlayerId?) = Account(ACCOUNT_ID, FULL_METAL_PUUID, owner)

        "an unowned account links to the player" {
            every { playerRepo.getById(OWNER) } returns fullMetal
            every { accountRepo.getById(ACCOUNT_ID) } returns accountOwnedBy(null)
            every { accountRepo.updatePlayerId(ACCOUNT_ID, OWNER) } returns accountOwnedBy(OWNER)

            service.linkAccountToPlayer(OWNER, ACCOUNT_ID) shouldBe fullMetal

            verify(exactly = 1) { accountRepo.updatePlayerId(ACCOUNT_ID, OWNER) }
        }

        "an account owned by another player is a conflict" {
            every { playerRepo.getById(OWNER) } returns fullMetal
            every { accountRepo.getById(ACCOUNT_ID) } returns accountOwnedBy(SOMEONE_ELSE)

            val ex = shouldThrow<IllegalStateException> { service.linkAccountToPlayer(OWNER, ACCOUNT_ID) }

            ex.message.toString() shouldContain "already owned"
        }

        "an account owned by another player is not quietly reassigned" {
            every { playerRepo.getById(OWNER) } returns fullMetal
            every { accountRepo.getById(ACCOUNT_ID) } returns accountOwnedBy(SOMEONE_ELSE)

            shouldThrow<IllegalStateException> { service.linkAccountToPlayer(OWNER, ACCOUNT_ID) }

            verify(exactly = 0) { accountRepo.updatePlayerId(any(), any()) }
        }

        "re-linking an account to the player who already owns it succeeds" {
            every { playerRepo.getById(OWNER) } returns fullMetal
            every { accountRepo.getById(ACCOUNT_ID) } returns accountOwnedBy(OWNER)
            every { accountRepo.updatePlayerId(ACCOUNT_ID, OWNER) } returns accountOwnedBy(OWNER)

            service.linkAccountToPlayer(OWNER, ACCOUNT_ID) shouldBe fullMetal
        }

        "an unknown account is not found" {
            every { playerRepo.getById(OWNER) } returns fullMetal
            every { accountRepo.getById(ACCOUNT_ID) } returns null

            shouldThrow<NoSuchElementException> { service.linkAccountToPlayer(OWNER, ACCOUNT_ID) }
        }

        "an unknown player is not found, and the account is never read" {
            every { playerRepo.getById(OWNER) } returns null

            shouldThrow<NoSuchElementException> { service.linkAccountToPlayer(OWNER, ACCOUNT_ID) }

            verify(exactly = 0) { accountRepo.getById(any()) }
        }

        "a failed write surfaces as a database error rather than a silent success" {
            every { playerRepo.getById(OWNER) } returns fullMetal
            every { accountRepo.getById(ACCOUNT_ID) } returns accountOwnedBy(null)
            every { accountRepo.updatePlayerId(ACCOUNT_ID, OWNER) } returns null

            shouldThrow<DatabaseException> { service.linkAccountToPlayer(OWNER, ACCOUNT_ID) }
        }
    })
