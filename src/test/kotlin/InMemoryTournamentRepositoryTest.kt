import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.inmemory.InMemoryTournamentRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class InMemoryTournamentRepositoryTest : StringSpec({
    val repo = InMemoryTournamentRepository()
    beforeTest {
        repo.clear()
    }
    "create() returns a valid Tournament() with defaults" {
        val new = NewTournament()
        repo.create(new)
        val t = repo::tournaments.get()
        t.size shouldBe 1
        t[0] shouldBe Tournament(TournamentId(0), "", PickType.TOURNAMENT_DRAFT, MapType.SUMMONERS_RIFT)
    }
    "create() allows ARAM override" {
        val new = NewTournament(pickType = PickType.ALL_RANDOM, mapType = MapType.HOWLING_ABYSS)
        repo.create(new)
        val t = repo::tournaments.get()
        t[0] shouldBe Tournament(TournamentId(0), "", PickType.ALL_RANDOM, MapType.HOWLING_ABYSS)
    }
})