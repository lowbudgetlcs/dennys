package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.models.types.EventDescription
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import java.time.Instant

data class EventUpdate(
    val name: EventName? = null,
    val description: EventDescription? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val status: EventStatus? = null,
    val eventGroupId: PatchField<EventGroupId?> = PatchField.Unset,
)
