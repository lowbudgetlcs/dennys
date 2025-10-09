package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.models.users.NewUser
import com.lowbudgetlcs.domain.models.users.Password
import com.lowbudgetlcs.domain.models.users.User
import com.lowbudgetlcs.domain.models.users.Username
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.user.IUserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class UserService(
    private val userRepo: IUserRepository,
) : IUserService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createUser(user: NewUser): User {
        logger.info("Creating new user...")
        if (isUsernameTaken(
                user.username,
            )
        ) {
            throw IllegalArgumentException("Username '${user.username.value}' is already in use.")
        }
        val salt = generateSalt()
        val hash = hashPassword(user.password, salt)
        val user = userRepo.insert(user, hash, salt) ?: throw DatabaseException("Failed to create user.")
        return user
    }

    override fun login(
        username: Username,
        password: Password,
    ): Boolean? {
        logger.info("Login attempt...")
        val user =
            userRepo.getByName(username) ?: throw NoSuchElementException("Username '${username.value}' does not exist.")
        val userPasswordHash = userRepo.getPasswordById(user.id) ?: throw DatabaseException("Login failed.")
        val hash = hashPassword(password, user.salt)
        if (hash != userPasswordHash.value) return null
        return true
        // TODO: JWT token creation
    }

    fun isUsernameTaken(username: Username): Boolean {
        logger.debug("Checking if '$username' is available...")
        return userRepo.getByName(username) != null
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return String(salt)
    }

    private fun hashPassword(
        password: Password,
        salt: String,
    ): String {
        val salted = "${password.value}$salt".toByteArray()
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val spec = PBEKeySpec(password.value.toCharArray(), salted, 120_000, 256)
        val key = factory.generateSecret(spec)
        val hash = key.encoded
        return hash.toHexString()
    }
}
