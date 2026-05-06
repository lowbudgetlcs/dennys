package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.model.types.EventDescription
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventName
import java.time.Instant

data class EventUpdate(
    val name: EventName? = null,
    val description: EventDescription? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val status: EventStatus? = null,
    val eventGroupId: PatchField<EventGroupId?> = PatchField.Unset,
)
