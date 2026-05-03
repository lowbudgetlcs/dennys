package com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.auth.core.models.Session

data class UserPrincipal(
    val userId: Int,
    val username: String,
    val roles: Set<String>,
)
