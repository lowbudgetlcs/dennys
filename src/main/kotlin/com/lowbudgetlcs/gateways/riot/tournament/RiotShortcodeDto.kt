package com.lowbudgetlcs.gateways.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotShortcodeDto(
    val codes: List<String>,
)
