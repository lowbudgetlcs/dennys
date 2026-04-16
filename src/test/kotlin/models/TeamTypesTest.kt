package models

import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.domain.team.models.types.TEAM_NAME_MAX_LENGTH
import com.lowbudgetlcs.domain.team.models.types.TEAM_NAME_MIN_LENGTH
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class TeamTypesTest :
    StringSpec({
        "TeamName cannot be empty" {
            shouldThrow<IllegalArgumentException> {
                "".toTeamName()
            }
        }

        "TeamName cannot exceed $TEAM_NAME_MAX_LENGTH characters" {
            shouldThrow<IllegalArgumentException> {
                "1".repeat(TEAM_NAME_MAX_LENGTH + 1).toTeamName()
            }
        }

        "TeamName must be at least $TEAM_NAME_MIN_LENGTH characters" {
            shouldThrow<IllegalArgumentException> {
                "1".repeat(TEAM_NAME_MIN_LENGTH - 1).toTeamName()
            }
        }

        "Valid PlayerName does not throw" {
            shouldNotThrow<IllegalArgumentException> {
                "1".repeat(TEAM_NAME_MAX_LENGTH).toTeamName()
                "1".repeat(TEAM_NAME_MIN_LENGTH).toTeamName()
                "Valid team name!".toTeamName()
            }
        }
    })
