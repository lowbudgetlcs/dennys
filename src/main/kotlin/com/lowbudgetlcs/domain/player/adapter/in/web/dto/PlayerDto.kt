package com.lowbudgetlcs.domain.player.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.account.adapter.`in`.web.dto.AccountDto
import com.lowbudgetlcs.domain.player.core.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Int,
    val name: String,
    val accounts: List<AccountDto>,
)

// Extensions
// TODO: Remove accounts from API contracts, or add PlayerWithAccounts back.
fun Player.toDto(): PlayerDto = PlayerDto(
    id = id.value,
    name = name.value,
    accounts = listOf(),
)
