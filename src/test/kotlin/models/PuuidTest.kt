package models

import com.lowbudgetlcs.domain.account.models.toPuuid
import com.lowbudgetlcs.domain.account.models.types.PUUID_LENGTH
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/** A real PUUID, from the account whose registration this validation had to keep working. */
private const val VALID_PUUID = "wtATP4ScufpCsr8o7FaDd2wQu5u3dZjPWLiU-W0dJ41EVO_JuSrDDBV680ygdBARc7H7GARUTtJjeA"

/** Builds a [PUUID_LENGTH]-character string ending in [suffix], so only the charset is under test. */
private fun paddedWith(suffix: String) = "a".repeat(PUUID_LENGTH - suffix.length) + suffix

class PuuidTest :
    StringSpec({
        "a real PUUID is accepted, and keeps its value" {
            shouldNotThrow<IllegalArgumentException> { VALID_PUUID.toPuuid() }
            VALID_PUUID.toPuuid().value shouldBe VALID_PUUID
        }

        "letters, digits, '-' and '_' are all valid" {
            shouldNotThrow<IllegalArgumentException> { paddedWith("aZ09-_").toPuuid() }
        }

        "a PUUID one character short is rejected" {
            shouldThrow<IllegalArgumentException> { "a".repeat(PUUID_LENGTH - 1).toPuuid() }
        }

        "a PUUID one character long is rejected" {
            shouldThrow<IllegalArgumentException> { "a".repeat(PUUID_LENGTH + 1).toPuuid() }
        }

        "an empty PUUID is rejected" {
            shouldThrow<IllegalArgumentException> { "".toPuuid() }
        }

        // The value is appended to a Riot URL as a path segment. Correct length is not enough:
        // these would each steer the request somewhere other than the account resource.
        "a path traversal of the right length is rejected" {
            shouldThrow<IllegalArgumentException> { paddedWith("/../../lol").toPuuid() }
        }

        "a query string of the right length is rejected" {
            shouldThrow<IllegalArgumentException> { paddedWith("?api_key=x").toPuuid() }
        }

        "a fragment of the right length is rejected" {
            shouldThrow<IllegalArgumentException> { paddedWith("#frag").toPuuid() }
        }

        "a percent-encoded slash of the right length is rejected" {
            shouldThrow<IllegalArgumentException> { paddedWith("%2F..").toPuuid() }
        }

        "whitespace of the right length is rejected" {
            shouldThrow<IllegalArgumentException> { paddedWith("  ").toPuuid() }
        }

        "the wrapper's own toString is not a valid PUUID" {
            // Guards the shape of the original bug: had this ever round-tripped, the malformed
            // URL would have been accepted as a legitimate value.
            shouldThrow<IllegalArgumentException> { VALID_PUUID.toPuuid().toString().toPuuid() }
        }
    })
