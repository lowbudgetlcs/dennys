package com.lowbudgetlcs.api.routes.v1.player.dto

import com.lowbudgetlcs.api.routes.v1.account.dto.AccountDto
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Int,
    val name: String,
    val accounts: List<AccountDto>,
)
