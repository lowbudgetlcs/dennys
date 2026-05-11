package com.lowbudgetlcs.api.dto.teams

import com.lowbudgetlcs.api.dto.UNSET_PATCH_FIELD
import kotlinx.serialization.Serializable

@Serializable
data class PatchTeamDto(
    val name: String? = null,
    val logo: String? = UNSET_PATCH_FIELD
)
