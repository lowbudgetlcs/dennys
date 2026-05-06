package com.lowbudgetlcs.domain.division.core.services

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.RepositoryException
import com.lowbudgetlcs.domain.division.core.model.EventGroup
import com.lowbudgetlcs.domain.division.core.model.EventGroupUpdate
import com.lowbudgetlcs.domain.division.core.model.EventGroupWithEvents
import com.lowbudgetlcs.domain.division.core.model.EventUpdate
import com.lowbudgetlcs.domain.division.core.model.NewEventGroup
import com.lowbudgetlcs.domain.division.core.model.patch
import com.lowbudgetlcs.domain.division.core.model.toEventGroupWithEvents
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupName
import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.division.core.port.IEventGroupRepository
import com.lowbudgetlcs.domain.division.core.port.IEventRepository
import com.lowbudgetlcs.logger

class EventGroupService(
    private val eventGroupRepo: IEventGroupRepository,
    private val eventRepo: IEventRepository,
) {

    /**
     * Fetches all event groups.
     *
     * @return a list containing all event groups.
     */
    suspend fun getAllEventGroups(): List<EventGroup> {
        logger.debug("Fetching all event groups...")
        return eventGroupRepo.getAll()
    }

    /**
     * Fetches an event group with its related events.
     *
     * @param id the event group to fetch.
     * @return the specified event group with all related events.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    suspend fun getEventGroupWithEvents(id: EventGroupId): EventGroupWithEvents {
        logger.debug("Getting event group by '$id' (with events)...")
        val group =
            eventGroupRepo.getById(id) ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
        val events = eventRepo.getAllByGroupId(group.id)
        return group.toEventGroupWithEvents(events)
    }

    /**
     * Fetches the specified event group.
     *
     * @param EventId the id of the event group.
     * @return the specified event group.
     *
     * @throws NoSuchElementException when the event group is not found.
     * @throws com.lowbudgetlcs.domain.RepositoryException when the underlying repository fails.
     */
    suspend fun getEventGroup(id: EventGroupId): EventGroup {
        logger.debug("Getting event group by '$id'...")
        return eventGroupRepo.getById(id)
            ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
    }

    /**
     * Create a new event group.
     *
     * @param group The new event group's details.
     * @return the newly created event group.
     *
     * @throws IllegalArgumentException if the event group cannot be created.
     * @throws com.lowbudgetlcs.domain.RepositoryException when the underlying repository fails.
     */
    suspend fun createEventGroup(group: NewEventGroup): EventGroup {
        logger.debug("Creating new event group...")
        logger.debug(group.toString())
        require(!isNameTaken(group.name)) { "Event group with name '${group.name}' already exists." }
        val errors = mutableListOf<String>()
        group.events?.forEach { eventId ->
            eventRepo.getById(eventId) ?: errors.add("Event with id '${eventId.value}' not found.")
        }
        require(errors.isEmpty()) { errors.joinToString(",") }
        val created = eventGroupRepo.insert(group) ?: throw RepositoryException("Failed to create event group.")
        group.events?.forEach { eventId -> addEvent(created.id, eventId) }
        return created
    }

    /**
     * Updates event group details.
     *
     * @param id the event to update.
     * @param update the new event information.
     * @return the updated event.
     *
     * @throws IllegalArgumentException if the new details are invalid.
     * @throws RepositoryException when the underlying repositories fail.
     */
    suspend fun patchEventGroup(
        id: EventGroupId,
        update: EventGroupUpdate,
    ): EventGroup {
        logger.debug("Patching event group '$id'...")
        val group =
            eventGroupRepo.getById(id) ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
        val name = update.name ?: group.name
        require(!isNameTaken(name)) { "Event group with name '$name' already exists." }
        return eventGroupRepo.update(group.patch(update))
            ?: throw RepositoryException("Failed to patch event group with id '${id.value}.")
    }

    /**
     * Add an event to an event group.
     *
     * @param eventGroupId the containing event group.
     * @param eventId the event to add.
     * @return the updated event group with all related events.
     *
     * @throws NoSuchElementException if the specified event group or event doesn't exist
     */
    suspend fun addEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents {
        logger.info("Adding '$eventId' to event group '$eventGroupId'...")
        val group =
            eventGroupRepo.getById(eventGroupId)
                ?: throw NoSuchElementException("Event group with id '${eventGroupId.value}' not found.")
        val event =
            eventRepo.getById(eventId) ?: throw NoSuchElementException("Event with id '${eventId.value}' not found.")
        eventRepo.update(event, EventUpdate(eventGroupId = PatchField.Value(group.id)))
            ?: throw RepositoryException("Failed to add event to event group.")
        return getEventGroupWithEvents(eventGroupId)
    }

    /**
     * Remove an event to an event group.
     *
     * @param eventGroupId the containing event group.
     * @param eventId the event to remove.
     * @return the updated event group with all related events.
     *
     * @throws NoSuchElementException if the specified event group or event doesn't exist
     */
    suspend fun removeEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents {
        logger.info("Removing '$eventId' from event group '$eventGroupId'...")
        val event =
            eventRepo.getById(eventId) ?: throw NoSuchElementException("Event with id '${eventId.value}' not found.")
        eventRepo.update(
            event,
            EventUpdate(eventGroupId = PatchField.Value(null)),
        )
            ?: throw RepositoryException("Failed to remove event from event group.")
        return getEventGroupWithEvents(eventGroupId)
    }

    /**
     * Checks if an event name is taken.
     *
     * @param name the name of the event.
     * @return True if name is taken.
     */
    suspend fun isNameTaken(name: EventGroupName): Boolean {
        logger.debug("Checking if '$name' is available...")
        return eventGroupRepo.getByName(name) != null
    }
}
