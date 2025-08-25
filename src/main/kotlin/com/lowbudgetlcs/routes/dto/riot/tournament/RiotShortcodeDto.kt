package com.lowbudgetlcs.routes.dto.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotShortcodeDto(val codes: List<String>)