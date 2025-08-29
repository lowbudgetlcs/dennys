package services

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.toPlayerName
import com.lowbudgetlcs.domain.services.player.PlayerService
import com.lowbudgetlcs.repositories.inmemory.InMemoryAccountRepository
import com.lowbudgetlcs.repositories.inmemory.InMemoryPlayerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PlayerServiceTest : StringSpec({

    val playerRepo = InMemoryPlayerRepository()
    val accountRepo = InMemoryAccountRepository()
    val service = PlayerService(playerRepo, accountRepo)

    beforeTest {
        playerRepo.clear()
        accountRepo.clear()
    }

    "Successfully create player" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        val created = service.createPlayer(newPlayer)

        created.shouldNotBeNull()
        created.name shouldBe newPlayer.name
        created.accounts shouldBe emptyList()

        // Ensure retrievable
        val fetched = service.getPlayer(created.id)
        fetched shouldBe created
    }

    "Creating duplicate player names fails" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        val created = service.createPlayer(newPlayer)
        created.shouldNotBeNull()

        val exception = shouldThrow<IllegalStateException> {
            service.createPlayer(newPlayer)
        }
        exception.message shouldBe "Player name already exists"
    }

    "Creating a blank player name throws" {
        val exception = shouldThrow<IllegalArgumentException> {
            val blankPlayer = NewPlayer("".toPlayerName())
            service.createPlayer(blankPlayer)
        }

        exception.message shouldBe "Player name cannot be blank"
    }

    "getAllPlayers returns created players" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        val p1 = service.createPlayer(newPlayer.copy(name = "one#AAA".toPlayerName()))
        val p2 = service.createPlayer(newPlayer.copy(name = "two#BBB".toPlayerName()))

        val all = service.getAllPlayers()
        all.shouldContainExactly(p1, p2)
    }

    "getPlayer throws for unknown ID" {
        val exception = shouldThrow<NoSuchElementException> {
            service.getPlayer(PlayerId(999))
        }
        exception.message shouldBe "Player not found"
    }

    "isNameTaken returns true for existing names" {
        val newPlayer = NewPlayer("player#123".toPlayerName())
        service.createPlayer(newPlayer)
        service.isNameTaken("player#123") shouldBe true
        service.isNameTaken("unknown#123") shouldBe false
    }

    "Blank or invalid Riot names should throw" {
        shouldThrow<IllegalArgumentException> {
            NewPlayer(name = "".toPlayerName())
        }
        // Add more invalid names here when you can think of any
    }

    "getAllPlayers() should return empty list when no players exist" {
        val players = service.getAllPlayers()
        players.shouldBeEmpty()
    }

    "getAllPlayers() should return all players with accounts" {
        val player1 = NewPlayer("PlayerOne#AAA".toPlayerName())
        val player2 = NewPlayer("PlayerTwo#BBB".toPlayerName())

        val created1 = service.createPlayer(player1)
        val created2 = service.createPlayer(player2)

        val all = service.getAllPlayers()

        all.map { it.name.value } shouldContainExactly listOf("PlayerOne#AAA", "PlayerTwo#BBB")
        all.any { it.accounts.isEmpty() } shouldBe true
    }

    "getAllPlayers() should reflect newly added players" {
        val initial = service.getAllPlayers()
        initial.shouldBeEmpty()

        val player = NewPlayer("NewPlayer#XYZ".toPlayerName())
        val created = service.createPlayer(player)

        val updated = service.getAllPlayers()
        updated.map { it.id } shouldContainExactly listOf(created.id)
    }

    "getPlayer should return correct player among many" {
        val one = service.createPlayer(NewPlayer("One#A".toPlayerName()))!!
        val two = service.createPlayer(NewPlayer("Two#B".toPlayerName()))!!

        service.getPlayer(one.id) shouldBe one
        service.getPlayer(two.id) shouldBe two
    }

    "renamePlayer should succeed for valid input" {
        val player = service.createPlayer(NewPlayer("OldName#123".toPlayerName()))
        val updated = service.renamePlayer(player.id, "NewName#123")

        updated.id shouldBe player.id
        updated.name.value shouldBe "NewName#123"
    }

    "renamePlayer should throw if name is blank" {
        val player = service.createPlayer(NewPlayer("Player#1".toPlayerName()))

        val exception = shouldThrow<IllegalArgumentException> {
            service.renamePlayer(player.id, "")
        }
        exception.message shouldBe "Player name cannot be blank"
    }

    "renamePlayer should throw if name already taken" {
        service.createPlayer(NewPlayer("Taken#1".toPlayerName()))
        val player = service.createPlayer(NewPlayer("Free#1".toPlayerName()))

        val exception = shouldThrow<IllegalStateException> {
            service.renamePlayer(player.id, "Taken#1")
        }
        exception.message shouldBe "Player name already exists"
    }

    "renamePlayer should throw for nonexistent player ID" {
        val exception = shouldThrow<NoSuchElementException> {
            service.renamePlayer(PlayerId(9999), "AnyName#123")
        }
        exception.message shouldBe "Player not found"
    }

    "renamePlayer should update the stored value" {
        val created = service.createPlayer(NewPlayer("Initial#Name".toPlayerName()))!!
        service.renamePlayer(created.id, "Updated#Name")

        val fetched = service.getPlayer(created.id)
        fetched.shouldNotBeNull()
        fetched.name.value shouldBe "Updated#Name"
    }

    "getAllPlayers should reflect additions" {
        service.getAllPlayers().shouldBeEmpty()

        val p1 = service.createPlayer(NewPlayer("One#1".toPlayerName()))
        val p2 = service.createPlayer(NewPlayer("Two#2".toPlayerName()))

        service.getAllPlayers().map { it.id } shouldContainExactly listOf(p1.id, p2.id)
    }
})