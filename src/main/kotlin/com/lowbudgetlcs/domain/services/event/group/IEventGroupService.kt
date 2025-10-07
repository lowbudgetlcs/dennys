package com.lowbudgetlcs.domain.services.event.group

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupUpdate
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.repositories.DatabaseException

interface IEventGroupService {
    /**
     * Fetches all event groups.
     *
     * @return a list containing all event groups.
     */
    fun getAllEventGroups(): List<EventGroup>

    /**
     * Fetches the specified event group.
     *
     * @param EventId the id of the event group.
     * @return the specified event group.
     *
     * @throws NoSuchElementException when the event group is not found.
     * @throws DatabaseException when the underlying repository fails.
     */
    fun getEventGroup(id: EventGroupId): EventGroup

    /**
     * Create a new event group.
     *
     * @param group The new event group's details.
     * @return the newly created event group.
     *
     * @throws IllegalArgumentException if the event group cannot be created.
     * @throws DatabaseException when the underlying repository fails.
     */
    fun createEventGroup(group: NewEventGroup): EventGroup

    /**
     * Updates event group details.
     *
     * @param id the event to update.
     * @param update the new event information.
     * @return the updated event.
     *
     * @throws IllegalArgumentException if the new details are invalid.
     * @throws DatabaseException when the underlying repositories fail.
     */
    fun patchEventGroup(
        id: EventGroupId,
        update: EventGroupUpdate,
    ): EventGroup

    /**
     * Fetches an event group with its related events.
     *
     * @param id the event group to fetch.
     * @return the specified event group with all related events.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    fun getEventGroupWithEvents(id: EventGroupId): EventGroupWithEvents

    /**
     * Add an event to an event group.
     *
     * @param eventGroupId the containing event group.
     * @param eventId the event to add.
     * @return the updated event group with all related events.
     *
     * @throws NoSuchElementException if the specified event group or event doesn't exist
     */
    fun addEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents

    /**
     * Remove an event to an event group.
     *
     * @param eventGroupId the containing event group.
     * @param eventId the event to remove.
     * @return the updated event group with all related events.
     *
     * @throws NoSuchElementException if the specified event group or event doesn't exist
     */
    fun removeEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents
}
