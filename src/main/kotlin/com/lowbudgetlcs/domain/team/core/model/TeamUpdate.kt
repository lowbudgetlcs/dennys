package com.lowbudgetlcs.domain.team.core.model

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName

data class TeamUpdate(
    val name: TeamName? = null,
    val logo: PatchField<String?> = PatchField.Unset,
    val eventId: PatchField<EventId?> = PatchField.Unset,
)
