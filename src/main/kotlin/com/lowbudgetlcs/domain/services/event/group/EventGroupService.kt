package com.lowbudgetlcs.domain.services.event.group

import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.group.EventGroup
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.group.EventGroupName
import com.lowbudgetlcs.domain.models.events.group.EventGroupUpdate
import com.lowbudgetlcs.domain.models.events.group.EventGroupWithEvents
import com.lowbudgetlcs.domain.models.events.group.NewEventGroup
import com.lowbudgetlcs.domain.models.events.group.patch
import com.lowbudgetlcs.domain.models.events.group.toEventGroupWithEvents
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.event.group.IEventGroupRepository
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
        if (isNameTaken(group.name)) throw IllegalArgumentException("Event group '${group.name}' already exists.")
        return eventGroupRepo.insert(group) ?: throw DatabaseException("Failed to create event group.")
    }

    override fun patchEventGroup(
        id: EventGroupId,
        update: EventGroupUpdate,
    ): EventGroup {
        logger.debug("Patching event group '$id'...")
        val group =
            eventGroupRepo.getById(id) ?: throw NoSuchElementException("Event group with id '${id.value}' not found.")
        val name = update.name ?: group.name
        if (isNameTaken(name)) throw IllegalArgumentException("Event group '$name' already exists.")
        return eventGroupRepo.update(group.patch(update))
            ?: throw DatabaseException("Failed to patch event group with id '${id.value}.")
    }

    override fun addEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents {
        TODO("Not yet implemented")
    }

    override fun removeEvent(
        eventGroupId: EventGroupId,
        eventId: EventId,
    ): EventGroupWithEvents {
        TODO("Not yet implemented")
    }

    /**
     * Checks if an event name is taken.
     * @return false if name is not taken.
     * @throws IllegalArgumentException when name already exists.
     */
    fun isNameTaken(name: EventGroupName): Boolean {
        logger.debug("Checking if '$name' is available...")
        return eventGroupRepo.getByName(name) != null
    }
}
