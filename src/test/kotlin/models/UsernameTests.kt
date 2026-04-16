package models

import com.lowbudgetlcs.domain.user.models.toUsername
import com.lowbudgetlcs.domain.user.models.types.USER_NAME_MAX_LENGTH
import com.lowbudgetlcs.domain.user.models.types.USER_NAME_MIN_LENGTH
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class UsernameTests : StringSpec({
    "Username cannot be empty." {
        shouldThrow<IllegalArgumentException> {
            "".toUsername()
        }
    }

    "Username cannot be greater than $USER_NAME_MAX_LENGTH characters." {
        shouldThrow<IllegalArgumentException> {
            "a".repeat(USER_NAME_MAX_LENGTH + 1).toUsername()
        }
    }
    "Username cannot be less than $USER_NAME_MIN_LENGTH characters." {
        shouldThrow<IllegalArgumentException> {
            "a".repeat(USER_NAME_MIN_LENGTH - 1).toUsername()
        }
    }

    "Valid username succeeds." {
        shouldNotThrow<IllegalArgumentException> {
            "a".repeat(USER_NAME_MAX_LENGTH).toUsername()
            "a".repeat(USER_NAME_MIN_LENGTH).toUsername()
            "ruuffian".toUsername()
        }
    }
})
