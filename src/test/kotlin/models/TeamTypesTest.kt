package models

import com.lowbudgetlcs.domain.team.models.toTeamName
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class TeamTypesTest :
    StringSpec({
        "TeamName cannot be empty" {
            shouldThrow<IllegalArgumentException> {
                "".toTeamName()
            }
        }

        "TeamName cannot exceed 80 characters" {
            shouldThrow<IllegalArgumentException> {
                "".repeat(81).toTeamName()
            }
        }
    })
