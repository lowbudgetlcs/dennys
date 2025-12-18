import com.lowbudgetlcs.api.auth.PasswordHasher
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PasswordHashingTest :
    StringSpec({
        val hasher = PasswordHasher()

        "Create a hash given string input" {
            val input = "ABCD"
            val a = hasher.hash(input)
            a.shouldNotBeNull()
            a shouldNotBeEqual ""
        }

        "verify() returns true when passed original string" {
            val input = "ABCD"
            val hash = hasher.hash(input)
            hasher.verify(input, hash) shouldBe true
        }

        "verify() returns false when passed different string" {
            val input = "ABCD"
            val hash = hasher.hash(input)
            hasher.verify("1234", hash) shouldBe false
        }
    })
