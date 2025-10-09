package services

import com.lowbudgetlcs.domain.models.users.NewUser
import com.lowbudgetlcs.domain.models.users.toPassword
import com.lowbudgetlcs.domain.models.users.toUsername
import com.lowbudgetlcs.domain.services.user.UserService
import com.lowbudgetlcs.repositories.user.UserRepository
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.mockk.mockk

class UserServiceTest :
    FunSpec({
        val userRepo = mockk<UserRepository>()
        val service = UserService(userRepo)
        val newUser = NewUser("ruuffian".toUsername(), password = "Temp_12345".toPassword())

        test("Empty username not allowed.") {
            shouldThrowAny {
                "".toUsername()
            }
        }
        test("Empty password not allowed.") {
            val exception =
                shouldThrowAny {
                    "".toPassword()
                }
            exception.message.shouldContain("empty")
        }
        test("Short password not allowed.") {
            val exception =
                shouldThrowAny {
                    "abC1".toPassword()
                }
            exception.message.shouldContain("8")
        }

        test("Password must contain a number.") {
            val exception =
                shouldThrowAny {
                    "aaaabbbb".toPassword()
                }
            exception.message.shouldContain("number")
        }
        test("Password must contain a lower case character.") {
            val exception =
                shouldThrowAny {
                    "AAAA1111".toPassword()
                }
            exception.message.shouldContain("lower")
        }
        test("Password must contain an upper case character.") {
            val exception =
                shouldThrowAny {
                    "aaaa1111".toPassword()
                }
            exception.message.shouldContain("upper")
        }
    })
