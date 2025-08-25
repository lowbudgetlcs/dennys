package com.lowbudgetlcs.routes.dto.players

import com.lowbudgetlcs.routes.dto.accounts.AcountDto
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Int,
    val name: String,
    val accounts: List<AcountDto>
)
