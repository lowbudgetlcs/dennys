import com.lowbudgetlcs.hashing.Argon2Hasher
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PasswordHashingTest :
    FunSpec({
        val argon2Hasher = Argon2Hasher()

        test("Create a hash given string input") {
            val input = "ABCD"
            val a = argon2Hasher.hash(input)
            a.shouldNotBeNull()
            a shouldNotBeEqual ""
        }

        test("verify() returns true when passed original string") {
            val input = "ABCD"
            val hash = argon2Hasher.hash(input)
            argon2Hasher.verify(input, hash) shouldBe true
        }

        test("verify() returns false when passed different string") {
            val input = "ABCD"
            val hash = argon2Hasher.hash(input)
            argon2Hasher.verify("1234", hash) shouldBe false
        }
    })
