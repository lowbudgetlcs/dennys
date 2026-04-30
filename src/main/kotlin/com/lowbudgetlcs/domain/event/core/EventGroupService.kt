package com.lowbudgetlcs.domain.event.core

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.core.model.EventGroup
import com.lowbudgetlcs.domain.event.core.model.EventGroupUpdate
import com.lowbudgetlcs.domain.event.core.model.EventGroupWithEvents
import com.lowbudgetlcs.domain.event.core.model.EventUpdate
import com.lowbudgetlcs.domain.event.core.model.NewEventGroup
import com.lowbudgetlcs.domain.event.core.model.patch
import com.lowbudgetlcs.domain.event.core.model.toEventGroupWithEvents
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.event.core.port.IEventGroupRepository
import com.lowbudgetlcs.domain.event.core.port.IEventGroupService
import com.lowbudgetlcs.domain.event.core.port.IEventRepository
import com.lowbudgetlcs.repositories.DatabaseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EventGroupService(
    private val eventGroupRepo: IEventGroupRepository,
    private val eventRepo: IEventRepository,
) : IEventGroupService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllEventGroups(): List<EventGroup> {
        logger.debug("Fetching all event groups...")
        return eventGroupRepo.getAll()
    }

    override fun getEventGroupWithEvents(id: EventGroupId): EventGroupWithEvents {
        logger.debug("Getting event group by '$id' (with events)...")
        val group =
            eventGroupRepo.getById(id) ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
        val events = eventRepo.getAllByGroupId(group.id)
        return group.toEventGroupWithEvents(events)
    }

    override fun getEventGroup(id: EventGroupId): EventGroup {
        logger.debug("Getting event group by '$id'...")
        return eventGroupRepo.getById(id)
            ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
    }

    override fun createEventGroup(group: NewEventGroup): EventGroup {
        logger.debug("Creating new event group...")
        logger.debug(group.toString())
        require(!isNameTaken(group.name)) { "Event group with name '${group.name}' already exists." }
        val errors = mutableListOf<String>()
        group.events?.forEach { eventId ->
            eventRepo.getById(eventId) ?: errors.add("Event with id '${eventId.value}' not found.")
        }
        require(errors.isEmpty()) { errors.joinToString(",") }
        val created = eventGroupRepo.insert(group) ?: throw DatabaseException("Failed to create event group.")
        group.events?.forEach { eventId -> addEvent(created.id, eventId) }
        return created
    }

    override fun patchEventGroup(
        id: EventGroupId,
        update: EventGroupUpdate,
    ): EventGroup {
        logger.debug("Patching event group '$id'...")
        val group =
            eventGroupRepo.getById(id) ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
        val name = update.name ?: group.name
        require(!isNameTaken(name)) { "Event group with name '$name' already exists." }
        return eventGroupRepo.update(group.patch(update))
            ?: throw DatabaseException("Failed to patch event group with id '${id.value}.")
    }

    override fun addEvent(
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
            ?: throw DatabaseException("Failed to add event to event group.")
        return getEventGroupWithEvents(eventGroupId)
    }

    override fun removeEvent(
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
            ?: throw DatabaseException("Failed to remove event from event group.")
        return getEventGroupWithEvents(eventGroupId)
    }

    /**
     * Checks if an event name is taken.
     *
     * @param name the name of the event.
     * @return True if name is taken.
     */
    fun isNameTaken(name: EventGroupName): Boolean {
        logger.debug("Checking if '$name' is available...")
        return eventGroupRepo.getByName(name) != null
    }
}
