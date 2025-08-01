import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.models.toPlayerName
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.repositories.inmemory.InMemoryPlayerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PlayerServiceTest : StringSpec({

    val repo = InMemoryPlayerRepository()
    val service = PlayerService(repo)

    beforeTest {
        repo.clear()
    }

    val newPlayer = NewPlayer(
        name = "player#123".toPlayerName(),
        teamId = null
    )

    "Successfully create player" {
        val created = service.createPlayer(newPlayer)

        created.shouldNotBeNull()
        created.name shouldBe newPlayer.name
        created.accounts shouldBe emptyList()

        // Ensure retrievable
        val fetched = service.getPlayer(created.id)
        fetched shouldBe created
    }

    "Creating duplicate player names fails" {
        val created = service.createPlayer(newPlayer)
        created.shouldNotBeNull()

        val duplicate = service.createPlayer(newPlayer)
        duplicate.shouldBeNull()
    }

    "getAllPlayers returns created players" {
        val p1 = service.createPlayer(newPlayer.copy(name = "one#AAA".toPlayerName()))
        val p2 = service.createPlayer(newPlayer.copy(name = "two#BBB".toPlayerName()))

        val all = service.getAllPlayers()
        all.shouldContainExactly(p1, p2)
    }

    "getPlayer returns null for invalid ID" {
        val result = service.getPlayer(PlayerId(999))
        result.shouldBeNull()
    }

    "isNameTaken returns true for existing names" {
        service.createPlayer(newPlayer)
        service.isNameTaken("player#123") shouldBe true
        service.isNameTaken("unknown#123") shouldBe false
    }

    "Blank or invalid Riot names should throw" {
        shouldThrow<IllegalArgumentException> {
            NewPlayer(name = "".toPlayerName(), teamId = null)
        }
        // Add more invalid names here when you can think of any
    }

    "getAllPlayers() should return empty list when no players exist" {
        val players = service.getAllPlayers()
        players.shouldBeEmpty()
    }

    "getAllPlayers() should return all players with accounts" {
        val player1 = NewPlayer("PlayerOne#AAA".toPlayerName(), null)
        val player2 = NewPlayer("PlayerTwo#BBB".toPlayerName(), null)

        val created1 = service.createPlayer(player1)
        val created2 = service.createPlayer(player2)

        val all = service.getAllPlayers()

        all.map { it.name.name } shouldContainExactly listOf("PlayerOne#AAA", "PlayerTwo#BBB")
        all.any { it.accounts.isEmpty() } shouldBe true
    }

    "getAllPlayers() should reflect newly added players" {
        val initial = service.getAllPlayers()
        initial.shouldBeEmpty()

        val player = NewPlayer("NewPlayer#XYZ".toPlayerName(), null)
        val created = service.createPlayer(player)

        val updated = service.getAllPlayers()
        updated.map { it.id } shouldContainExactly listOf(created!!.id)
    }

    "getPlayer should return correct player among many" {
        val one = service.createPlayer(NewPlayer("One#A".toPlayerName(), null))!!
        val two = service.createPlayer(NewPlayer("Two#B".toPlayerName(), null))!!

        service.getPlayer(one.id) shouldBe one
        service.getPlayer(two.id) shouldBe two
    }

    "getPlayer returns null when ID does not exist" {
        val result = service.getPlayer(PlayerId(999))
        result.shouldBeNull()
    }

    "getPlayer returns null when ID was never used, even after inserting others" {
        val player1 = service.createPlayer(NewPlayer("Alpha#123".toPlayerName(), null))!!
        val player2 = service.createPlayer(NewPlayer("Bravo#456".toPlayerName(), null))!!

        val unknownId = PlayerId(9999)
        val result = service.getPlayer(unknownId)
        result.shouldBeNull()
    }
})