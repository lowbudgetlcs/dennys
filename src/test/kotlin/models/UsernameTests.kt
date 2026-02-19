package models

import com.lowbudgetlcs.domain.auth.models.types.Username
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class UsernameTests :
    StringSpec({
        "Username cannot be greater than 25 characters." {
            shouldThrow<IllegalArgumentException> {
                Username("a".repeat(26))
            }
        }
        "Username cannot be less than 3 characters." {
            shouldThrow<IllegalArgumentException> {
                Username("a".repeat(2))
            }
        }
        "Username cannot be empty." {
            shouldThrow<IllegalArgumentException> {
                Username("")
            }
        }
    })
