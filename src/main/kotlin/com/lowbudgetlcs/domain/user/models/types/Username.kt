package com.lowbudgetlcs.domain.user.models.types

const val USER_NAME_MAX_LENGTH = 25
const val USER_NAME_MIN_LENGTH = 3

@JvmInline
value class Username(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "UserName cannot be blank." }
        require(value.length <= USER_NAME_MAX_LENGTH) { "Username cannot exceed $USER_NAME_MAX_LENGTH characters." }
        require(value.length >= USER_NAME_MIN_LENGTH) { "Minimum username length is $USER_NAME_MIN_LENGTH characters." }
    }
}
