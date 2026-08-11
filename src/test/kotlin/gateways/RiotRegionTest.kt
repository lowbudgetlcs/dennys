package gateways

import com.lowbudgetlcs.gateways.riot.tournament.RiotRegion
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RiotRegionTest :
    StringSpec({
        "the region enum maps to its platformId" {
            RiotRegion.platformIdOf("NA") shouldBe "NA1"
            RiotRegion.platformIdOf("EUW") shouldBe "EUW1"
        }

        // Production games/by-code returns the platformId, not the enum name, and dropping it
        // leaves riot_match_id null, which disables both duplicate-match guards.
        "a platformId maps to itself" {
            RiotRegion.platformIdOf("NA1") shouldBe "NA1"
            RiotRegion.platformIdOf("EUW1") shouldBe "EUW1"
        }

        "matching is case insensitive either way" {
            RiotRegion.platformIdOf("na") shouldBe "NA1"
            RiotRegion.platformIdOf("na1") shouldBe "NA1"
        }

        "regions whose platformId equals their name still resolve" {
            RiotRegion.platformIdOf("KR") shouldBe "KR"
            RiotRegion.platformIdOf("RU") shouldBe "RU"
        }

        "an unrecognised region resolves to null" {
            RiotRegion.platformIdOf("ATLANTIS") shouldBe null
            RiotRegion.platformIdOf("") shouldBe null
        }
    })
