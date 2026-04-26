package com.lowbudgetlcs.api.routes.v1.team.dto

import com.lowbudgetlcs.api.UNSET_PATCH_FIELD
import kotlinx.serialization.Serializable

@Serializable
data class PatchTeamDto(
    val name: String? = null,
    val logo: String? = UNSET_PATCH_FIELD,
)
