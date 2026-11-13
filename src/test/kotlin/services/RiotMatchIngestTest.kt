package services

import com.lowbudgetlcs.domain.series.game.RiotMatchIngestService
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.match.IRiotMatchGateway
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.riotmatchdata.IRiotMatchDataRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

private fun game(
    id: Int,
    matchId: String,
) = Game(
    id = id.toGameId(),
    seriesId = SeriesId(1),
    tournamentCodeId = null,
    riotMatchId = matchId.toRiotMatchId(),
    number = 1,
    createdAt = Instant.EPOCH,
    result = null,
)

private fun payload(matchId: String) = buildJsonObject { put("matchId", JsonPrimitive(matchId)) }

private class IngestFixture(
    vararg games: Game,
) {
    val gameRepo = mockk<IGameRepository>()
    val gateway = mockk<IRiotMatchGateway>()
    val dataRepo = mockk<IRiotMatchDataRepository>()
    val service = RiotMatchIngestService(gameRepo, gateway, dataRepo)

    init {
        every { gameRepo.getMissingRiotMatchData(any()) } returns games.toList()
        every { dataRepo.save(any(), any(), any()) } returns null
    }
}

class RiotMatchIngestTest :
    StringSpec({
        "stores the match and timeline for a game missing Riot match data" {
            val f = IngestFixture(game(1, "NA1_1"))
            coEvery { f.gateway.getMatch("NA1_1".toRiotMatchId()) } returns payload("NA1_1")
            coEvery { f.gateway.getTimeline("NA1_1".toRiotMatchId()) } returns payload("timeline")

            f.service.ingestPending()

            verify { f.dataRepo.save(1.toGameId(), payload("NA1_1"), payload("timeline")) }
        }

        "a game Riot 404s is skipped and the next game is still stored" {
            val f = IngestFixture(game(1, "NA1_1"), game(2, "NA1_2"))
            coEvery { f.gateway.getMatch("NA1_1".toRiotMatchId()) } throws NoSuchElementException("404")
            coEvery { f.gateway.getMatch("NA1_2".toRiotMatchId()) } returns payload("NA1_2")
            coEvery { f.gateway.getTimeline("NA1_2".toRiotMatchId()) } returns payload("timeline")

            f.service.ingestPending()

            verify(exactly = 0) { f.dataRepo.save(1.toGameId(), any(), any()) }
            verify { f.dataRepo.save(2.toGameId(), payload("NA1_2"), payload("timeline")) }
        }

        "a failed timeline still stores the match" {
            val f = IngestFixture(game(1, "NA1_1"))
            coEvery { f.gateway.getMatch("NA1_1".toRiotMatchId()) } returns payload("NA1_1")
            coEvery { f.gateway.getTimeline("NA1_1".toRiotMatchId()) } throws RiotApiException("boom", 503)

            f.service.ingestPending()

            verify { f.dataRepo.save(1.toGameId(), payload("NA1_1"), null) }
        }
    })
