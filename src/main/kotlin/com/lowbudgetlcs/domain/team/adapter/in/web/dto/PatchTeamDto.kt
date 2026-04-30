package com.lowbudgetlcs.domain.team.adapter.`in`.web.dto

import com.lowbudgetlcs.api.dto.UNSET_PATCH_FIELD
import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.team.core.models.TeamUpdate
import com.lowbudgetlcs.domain.team.core.models.types.toTeamName
import kotlinx.serialization.Serializable

@Serializable
data class PatchTeamDto(
    val name: String? = null,
    val logo: String? = UNSET_PATCH_FIELD
)

// Extensions
fun PatchTeamDto.toTeamUpdate(): TeamUpdate =
    TeamUpdate(
        name = name?.toTeamName(),
        logo = when (logo) {
            UNSET_PATCH_FIELD -> {
                PatchField.Unset
            }

            else -> PatchField.Value(logo)
        }
    )
