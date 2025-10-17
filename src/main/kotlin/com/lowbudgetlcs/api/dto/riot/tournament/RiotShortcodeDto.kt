package com.lowbudgetlcs.api.dto.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotShortcodeDto(
    val codes: List<String>,
)
