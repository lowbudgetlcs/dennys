package com.lowbudgetlcs.api.dto.teams

import kotlinx.serialization.Serializable

@Serializable
data class PatchTeamDto(val name: String? = null, val logoName: String? = null)
