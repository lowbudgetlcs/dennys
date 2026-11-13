package gateways

import com.lowbudgetlcs.config.modules.riotJson
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGamesV5Dto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RiotJsonTest :
    StringSpec({
        "decodes a games-by-code entry with a field Denny's has no model for" {
            val payload =
                """
                {
                  "gameId": 5102531894,
                  "shortCode": "NA04eff-c5b0b774-c049-47cf-88f4-eee05c8d83ae",
                  "atakhan": { "kills": 1 }
                }
                """.trimIndent()

            val game = riotJson.decodeFromString<RiotTournamentGamesV5Dto>(payload)

            game.gameId shouldBe 5102531894L
        }
    })
