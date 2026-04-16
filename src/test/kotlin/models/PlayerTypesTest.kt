package models

import com.lowbudgetlcs.domain.player.models.toPlayerName
import com.lowbudgetlcs.domain.player.models.types.PLAYER_NAME_MAX_LENGTH
import com.lowbudgetlcs.domain.player.models.types.PLAYER_NAME_MIN_LENGTH
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class PlayerTypesTest :
    StringSpec({
        "PlayerName cannot be empty" {
            shouldThrow<IllegalArgumentException> {
                "".toPlayerName()
            }
        }

        "PlayerName cannot exceed $PLAYER_NAME_MAX_LENGTH characters" {
            shouldThrow<IllegalArgumentException> {
                "1".repeat(PLAYER_NAME_MAX_LENGTH + 1).toPlayerName()
            }
        }

        "PlayerName must be at least $PLAYER_NAME_MIN_LENGTH characters" {
            shouldThrow<IllegalArgumentException> {
                "1".repeat(PLAYER_NAME_MIN_LENGTH - 1).toPlayerName()
            }
        }

        "Valid PlayerName does not throw" {
            shouldNotThrow<IllegalArgumentException> {
                "1".repeat(PLAYER_NAME_MIN_LENGTH).toPlayerName()
                "1".repeat(PLAYER_NAME_MAX_LENGTH).toPlayerName()
                "ruuffian#FUNZ".toPlayerName()
            }
        }
    })
