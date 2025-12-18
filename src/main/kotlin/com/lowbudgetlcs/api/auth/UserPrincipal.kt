package com.lowbudgetlcs.api.auth

data class UserPrincipal(
    val userId: Int,
    val username: String,
    val roles: Set<String>,
)
