import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.gateways.MockTournamentGateway
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.types.shouldBeInstanceOf

class MockTournamentGatewayTest : StringSpec({
    val gate = MockTournamentGateway()
    "create() returns a valid Tournament()" {
        val new = NewTournament("Test")
        val t = gate.create(new)
        t.shouldBeInstanceOf<Tournament>()
        t.id.value shouldBeGreaterThan 0
    }
})