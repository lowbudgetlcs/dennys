import com.lowbudgetlcs.domain.tournament.MapType
import com.lowbudgetlcs.domain.tournament.NewTournament
import com.lowbudgetlcs.domain.tournament.PickType
import com.lowbudgetlcs.domain.tournament.Tournament
import com.lowbudgetlcs.repositories.inmemory.InMemoryTournamentRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class InMemoryTournamentRepositoryTest : StringSpec({
    val repo = InMemoryTournamentRepository()
    "create() returns a valid Tournament() with defaults" {
        val new = NewTournament()
        repo.create(new)
        val t = repo::tournaments.get()
        t.size shouldBe 1
        t[0] shouldBe Tournament(0, "", PickType.TOURNAMENT_DRAFT, MapType.SUMMONERS_RIFT)
    }
})