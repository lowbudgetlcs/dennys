import com.lowbudgetlcs.appConfig
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.gateways.TournamentGateway
import com.lowbudgetlcs.repositories.IMetadataRepository
import com.lowbudgetlcs.routes.dto.InstantSerializer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.Instant

class TournamentGatewayTest : FunSpec({
    val riotHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                serializersModule = SerializersModule {
                    contextual(Instant::class, InstantSerializer)
                }
            })
        }
    }
    val metadataRepo = mockk<IMetadataRepository>()
    every { metadataRepo.getProviderId() } returns 1
    val gate = TournamentGateway(
        metadataRepo = metadataRepo,
        client = riotHttpClient,
        apiKey = appConfig.riot.key,
        stub = appConfig.riot.useStubs,
    )
    val newTournament = NewTournament("Test")

    test("create() returns a valid Tournament") {
        val tournament = gate.create(newTournament)
        tournament.shouldBeInstanceOf<Tournament>()
        tournament.shouldBeEqualToIgnoringFields(Tournament(0.toTournamentId(), "Test"), Tournament::id)
    }
})