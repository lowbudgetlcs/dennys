package com.lowbudgetlcs.domain.team.core.models

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.team.core.models.types.TeamName

data class TeamUpdate(
    val name: TeamName? = null,
    val logo: PatchField<String?> = PatchField.Unset,
    val eventId: PatchField<EventId?> = PatchField.Unset,
)
