import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.domain.models.tournament.TournamentId
import com.lowbudgetlcs.repositories.inmemory.InMemoryTournamentRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class InMemoryTournamentRepositoryTest : StringSpec({
    val repo = InMemoryTournamentRepository()
    beforeTest {
        repo.clear()
    }
    "create() returns a valid Tournament()" {
        val new = NewTournament("Test")
        repo.create(new)
        val t = repo::tournaments.get()
        t.size shouldBe 1
        t[0] shouldBe Tournament(TournamentId(0), "Test")
    }
})