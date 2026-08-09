package models

import com.lowbudgetlcs.domain.series.game.models.types.RiotMatchId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RiotMatchIdTest :
    StringSpec({
        "accepts a realistic match id" {
            RiotMatchId("NA1_5102531894").value shouldBe "NA1_5102531894"
        }

        "rejects a blank id" {
            shouldThrow<IllegalArgumentException> { RiotMatchId("") }
            shouldThrow<IllegalArgumentException> { RiotMatchId("   ") }
        }

        // games/by-code returns the tournament region enum (NA) rather than a
        // platformId (NA1), so the pull path has to map it before building an id.
        "rejects an id with no separator" {
            shouldThrow<IllegalArgumentException> { RiotMatchId("NA15102531894") }
        }

        "rejects an id missing the platformId" {
            shouldThrow<IllegalArgumentException> { RiotMatchId("_5102531894") }
        }

        "rejects an id missing the gameId" {
            shouldThrow<IllegalArgumentException> { RiotMatchId("NA1_") }
        }

        "rejects an id with more than one separator" {
            shouldThrow<IllegalArgumentException> { RiotMatchId("NA1_510_2531894") }
        }
    })
