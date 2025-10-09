package com.lowbudgetlcs.domain.models.users

@JvmInline
value class UserId(
    val value: Int,
)

fun Int.toUserId(): UserId = UserId(this)

@JvmInline
value class Username(
    val value: String,
) {
    init {
        require(!value.isEmpty()) { "Username must not be empty." }
    }
}

fun String.toUsername(): Username = Username(this)

@JvmInline
value class Password(
    val value: String,
) {
    init {
        var hasDigit = false
        var hasUpper = false
        var hasLower = false

        for (c in value) {
            when {
                c.isDigit() -> hasDigit = true
                c.isUpperCase() -> hasUpper = true
                c.isLowerCase() -> hasLower = true
            }
        }

        require(value.isNotEmpty()) { "Password must not be empty." }
        require(value.length >= 8) { "Password minimum length is 8 characters." }
        require(hasDigit) { "Password must contain at least 1 number." }
        require(hasUpper) { "Password must contain at least 1 upper-case character." }
        require(hasLower) { "Password must contain at least 1 lower-case character." }
    }

    override fun toString(): String = "*".repeat(8)
}

fun String.toPassword(): Password = Password(this)

data class User(
    val id: UserId,
    val username: Username,
    val salt: String,
)
