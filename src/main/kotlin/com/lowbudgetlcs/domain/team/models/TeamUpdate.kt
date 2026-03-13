package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.team.models.types.TeamLogoName
import com.lowbudgetlcs.domain.team.models.types.TeamName

data class TeamUpdate(
    val name: TeamName? = null,
    val logoName: TeamLogoName? = null,
    val eventId: PatchField<EventId?> = PatchField.Unset
)
