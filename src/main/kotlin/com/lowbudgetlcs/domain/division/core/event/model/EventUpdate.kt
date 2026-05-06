package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.event.model.types.EventDescription
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventName
import java.time.Instant

data class EventUpdate(
    val name: EventName? = null,
    val description: EventDescription? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val status: EventStatus? = null,
    val eventGroupId: PatchField<EventGroupId?> = PatchField.Unset,
)
