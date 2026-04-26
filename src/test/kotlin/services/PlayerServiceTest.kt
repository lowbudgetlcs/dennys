package services

import com.lowbudgetlcs.domain.player.PlayerService
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.toPlayer
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class PlayerServiceTest :
    StringSpec({

        val playerRepo = mockk<IPlayerRepository>()
        val accountRepo = mockk<IAccountRepository>()
        val service = PlayerService(playerRepo, accountRepo)

        beforeEach { clearAllMocks() }

        "getAllPlayers() should return empty list when no players exist" {
            every { playerRepo.getAll() } returns listOf()
            val players = service.getAllPlayers()
            players.shouldBeEmpty()
        }

        "getAllPlayers returns created players" {
            val newPlayer1 = NewPlayer("player#123".toPlayerName())
            val newPlayer2 = NewPlayer("pla#125".toPlayerName())
            val expectedPlayers =
                listOf(
                    newPlayer1.toPlayer(0.toPlayerId()),
                    newPlayer2.toPlayer(1.toPlayerId()),
                )
            every { playerRepo.getAll() } returns expectedPlayers

            val all = service.getAllPlayers()
            all shouldContainExactly expectedPlayers
        }

        "Successfully create player" {
            val newPlayer = NewPlayer("player#123".toPlayerName())
            val expectedPlayer = newPlayer.toPlayer(0.toPlayerId())

            every { playerRepo.getByName(newPlayer.name) } returns null
            every { playerRepo.insert(newPlayer) } returns expectedPlayer
            val created = service.createPlayer(newPlayer)

            created.shouldNotBeNull()
            created shouldBe expectedPlayer
        }

        "Creating duplicate player names fails" {
            val newPlayer = NewPlayer("player#123".toPlayerName())
            val duplicatePlayer = newPlayer.toPlayer(0.toPlayerId())
            every { playerRepo.getByName(duplicatePlayer.name) } returns duplicatePlayer
            val exception =
                shouldThrow<IllegalStateException> {
                    service.createPlayer(newPlayer)
                }
            exception.message shouldBe "Player name already exists"
        }

        "isNameTaken returns true for existing names" {
            val newPlayer = NewPlayer("player#123".toPlayerName())
            every { playerRepo.getByName(newPlayer.name) } returns null
            service.isNameTaken(newPlayer.name) shouldBe false
            every { playerRepo.getByName(newPlayer.name) } returns newPlayer.toPlayer(0.toPlayerId())
            service.isNameTaken(newPlayer.name) shouldBe true
        }

        "getPlayer throws for unknown ID" {
            val unknownPlayer = 0.toPlayerId()
            every { playerRepo.getById(unknownPlayer) } throws NoSuchElementException("Player not found")
            val exception =
                shouldThrow<NoSuchElementException> {
                    service.getPlayer(unknownPlayer)
                }
            exception.message shouldBe "Player not found"
        }

        "renamePlayer should succeed for valid input" {
            val player =
                Player(
                    id = 0.toPlayerId(),
                    name = "player".toPlayerName(),
                )
            val newName = "new".toPlayerName()
            val newPlayer = player.copy(name = newName)
            every { playerRepo.getByName(player.name) } returns player
            every { playerRepo.getByName(newName) } returns null
            every { playerRepo.getById(player.id) } returns player
            every {
                playerRepo.renamePlayer(
                    player.id,
                    newName,
                )
            } returns newPlayer
            val updated = service.renamePlayer(player.id, newName)

            updated shouldBe newPlayer
        }

        "renamePlayer should throw if name already taken" {
            val player =
                Player(
                    id = 0.toPlayerId(),
                    name = "player".toPlayerName(),
                )
            every { playerRepo.getByName(player.name) } returns player
            every { playerRepo.getById(player.id) } returns player
            shouldThrow<IllegalStateException> {
                service.renamePlayer(player.id, player.name)
            }
        }

        "renamePlayer should throw for nonexistent player ID" {
            val name = "irrelevant".toPlayerName()
            every { playerRepo.getByName(name) } returns null
            every { playerRepo.getById(any()) } returns null
            shouldThrow<NoSuchElementException> {
                service.renamePlayer(9999.toPlayerId(), name)
            }
        }
    })
