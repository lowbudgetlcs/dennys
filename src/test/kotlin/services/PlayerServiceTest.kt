package services

import com.lowbudgetlcs.domain.models.player.*
import com.lowbudgetlcs.domain.services.player.PlayerService
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class PlayerServiceTest : StringSpec({

    val playerRepo = mockk<IPlayerRepository>()
    val accountRepo = mockk<IAccountRepository>()
    val service = PlayerService(playerRepo, accountRepo)

    "getAllPlayers() should return empty list when no players exist" {
        every { playerRepo.getAll() } returns listOf()
        val players = service.getAllPlayers()
        players.shouldBeEmpty()
    }

    "getAllPlayers returns created players" {
        val newPlayer1 = NewPlayer("player#123".toPlayerName())
        val newPlayer2 = NewPlayer("pla#125".toPlayerName())
        val expectedPlayers = listOf(
            newPlayer1.toPlayer(0.toPlayerId()).toPlayerWithAccounts(listOf()),
            newPlayer2.toPlayer(1.toPlayerId()).toPlayerWithAccounts(listOf())
        )
        every { playerRepo.getAll() } returns expectedPlayers

        val all = service.getAllPlayers()
        all shouldContainExactly expectedPlayers
    }

    "Successfully create player" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        val expectedPlayer = newPlayer.toPlayer(0.toPlayerId()).toPlayerWithAccounts(listOf())

        every { playerRepo.getAll() } returns listOf()
        every { playerRepo.insert(newPlayer) } returns expectedPlayer
        val created = service.createPlayer(newPlayer)

        created.shouldNotBeNull()
        created shouldBe expectedPlayer
    }

    "Creating duplicate player names fails" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        val duplicatePlayer = newPlayer.toPlayer(0.toPlayerId())
        every { playerRepo.getAll() } returns listOf(duplicatePlayer.toPlayerWithAccounts(listOf()))
        val exception = shouldThrow<IllegalStateException> {
            service.createPlayer(newPlayer)
        }
        exception.message shouldBe "Player name already exists"
    }

    "Creating a blank player name throws" {
        val exception = shouldThrow<IllegalArgumentException> {
            "".toPlayerName()
        }
        exception.message shouldBe "Player name cannot be blank"
    }

    "isNameTaken returns true for existing names" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        every { playerRepo.getAll() } returns listOf()
        service.isNameTaken(newPlayer.name.value) shouldBe false
        every { playerRepo.getAll() } returns listOf(newPlayer.toPlayer(0.toPlayerId()).toPlayerWithAccounts(listOf()))
        service.isNameTaken(newPlayer.name.value) shouldBe true
    }

    "getPlayer throws for unknown ID" {
        val unknownPlayer = 0.toPlayerId()
        every { playerRepo.getById(unknownPlayer) } throws NoSuchElementException("Player not found")
        val exception = shouldThrow<NoSuchElementException> {
            service.getPlayer(unknownPlayer)
        }
        exception.message shouldBe "Player not found"
    }

    "renamePlayer should succeed for valid input" {
        val player = Player(
            id = 0.toPlayerId(), name = "player".toPlayerName()
        ).toPlayerWithAccounts(listOf())
        val newName = "new".toPlayerName()
        val newPlayer = player.copy(name = newName)
        every { playerRepo.getAll() } returns listOf(player)
        every { playerRepo.getById(player.id) } returns player
        every {
            playerRepo.renamePlayer(
                player.id, newName
            )
        } returns player.copy(name = newName)
        val updated = service.renamePlayer(player.id, newName.value)

        updated shouldBe newPlayer
    }

    // NOTE: This test is eliminated by refactoring renamePlayer to accept PlayerName instead of String,
    "renamePlayer should throw if name is blank" {
        val player = Player(
            id = 0.toPlayerId(), name = "player".toPlayerName()
        ).toPlayerWithAccounts(listOf())
        every { playerRepo.getAll() } returns listOf(player)
        every { playerRepo.getById(player.id) } returns player
        val exception = shouldThrow<IllegalArgumentException> {
            service.renamePlayer(player.id, "")
        }
        exception.message shouldBe "Player name cannot be blank"
    }

    "renamePlayer should throw if name already taken" {
        val player = Player(
            id = 0.toPlayerId(), name = "player".toPlayerName()
        ).toPlayerWithAccounts(listOf())
        every { playerRepo.getAll() } returns listOf(player)
        every { playerRepo.getById(player.id) } returns player
        val exception = shouldThrow<IllegalStateException> {
            service.renamePlayer(player.id, player.name.value)
        }
        exception.message shouldBe "Player name already exists"
    }

    "renamePlayer should throw for nonexistent player ID" {
        every { playerRepo.getAll() } returns listOf()
        every { playerRepo.getById(any()) } returns null
        val exception = shouldThrow<NoSuchElementException> {
            service.renamePlayer(9999.toPlayerId(), "irrelevant")
        }
        exception.message shouldBe "Player not found"
    }
})