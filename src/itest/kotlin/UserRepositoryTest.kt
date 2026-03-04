import com.lowbudgetlcs.domain.user.models.NewUser
import com.lowbudgetlcs.domain.user.models.User
import com.lowbudgetlcs.domain.user.models.toUser
import com.lowbudgetlcs.domain.user.models.toUserId
import com.lowbudgetlcs.domain.user.models.toUsername
import com.lowbudgetlcs.hashing.Argon2Hasher
import com.lowbudgetlcs.repositories.user.UserRepostitory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.runBlocking
import org.jooq.SQLDialect
import org.jooq.exception.IntegrityConstraintViolationException
import org.jooq.impl.DSL
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class UserRepositoryTest :
    StringSpec({
        val postgres =
            PostgreSQLContainer("postgres:15-alpine").apply {
                withCopyFileToContainer(MountableFile.forClasspathResource("sql"), "/docker-entrypoint-initdb.d/")
            }
        val ds =
            install(JdbcDatabaseContainerSpecExtension(postgres)) {
                maximumPoolSize = 1
            }
        val dslContext = DSL.using(ds, SQLDialect.POSTGRES)
        val repo = UserRepostitory(dslContext)

        val argon2Hasher = Argon2Hasher()
        val username = "ruuffian".toUsername()
        val password = "ABCD1**"
        val passwordHash = runBlocking { argon2Hasher.hash(password) }
        val username2 = "zain".toUsername()

        // Data
        val newUser =
            NewUser(
                username = username,
                passwordHash = passwordHash,
                roles = setOf("admin", "user"),
            )

        val newUser2 = NewUser(username2, passwordHash, setOf("user"))

        "getAll() starts empty" {
            val users = repo.getAll()
            users.shouldBeInstanceOf<List<User>>()
            users.shouldBeEmpty()
        }

        "insert() creates new user" {
            val user = repo.insert(newUser)
            user.shouldNotBeNull()
            user.shouldBeEqualToIgnoringFields(
                newUser.toUser(0.toUserId()),
                User::id,
            )
        }

        "getById() fetches correct User" {
            val created = repo.insert(newUser2)
            created.shouldNotBeNull()
            val user = repo.getById(created.id)
            user shouldBe created
        }

        "getAll() returns all users" {
            val users = repo.getAll()
            users.shouldBeInstanceOf<List<User>>()
            users.shouldHaveSize(2)
        }

        "usernames must be unique" {
            shouldThrow<IntegrityConstraintViolationException> {
                repo.insert(newUser)
            }
        }
    })
