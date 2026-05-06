package com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto

data class UserPrincipal(
    val userId: Int,
    val username: String,
    val roles: Set<String>,
)
