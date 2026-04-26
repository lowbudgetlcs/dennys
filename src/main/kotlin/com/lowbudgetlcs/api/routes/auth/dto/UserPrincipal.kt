package com.lowbudgetlcs.api.routes.auth.dto

data class UserPrincipal(
    val userId: Int,
    val username: String,
    val roles: Set<String>,
)
