package com.lowbudgetlcs.domain.auth.models.types

@JvmInline
value class Username(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "UserName cannot be blank." }
        require(value.length < 25) { "Username cannot be over 25 characters." }
        require(value.length > 3) { "Username cannot be under 3 characters." }
    }
}
