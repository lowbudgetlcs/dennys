package com.lowbudgetlcs.api.dto.players

import com.lowbudgetlcs.domain.account.dto.AccountDto
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Int,
    val name: String,
    val accounts: List<AccountDto>,
)
