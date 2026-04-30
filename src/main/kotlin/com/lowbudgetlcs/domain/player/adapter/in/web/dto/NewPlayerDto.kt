package com.lowbudgetlcs.domain.player.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName
import kotlinx.serialization.Serializable

@Serializable
data class NewPlayerDto(
    val name: String,
)

// Extensions
fun NewPlayerDto.toNewPlayer(): NewPlayer = NewPlayer(
    name = PlayerName(name),
)
