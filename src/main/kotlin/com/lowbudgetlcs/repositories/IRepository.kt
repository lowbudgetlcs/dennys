package com.lowbudgetlcs.repositories

/**
 *
 * Parent of all data repositories.
 *
 * A data-source agnostic contract for repositories.
 *
 * @param[T] The type of objects contained in this repository.
 * @param[K] The type of [T]'s id property.
 */
interface IRepository<T, K> {
    /**
     * @return List of all stored objects.
     */
    fun readAll(): List<T>

    /**
     * @param[id] A key of an object to fetch in storage.
     * @return An instance of [T] if [id] exists, null otherwise.
     */
    fun readById(id: K): T?

    /**
     * Filter all [T] in storage with a client-supplied [ICriteria].
     *
     * @param[criteria] ICriteria to apply to [T].
     * @return A list of [T] that match the given criteria, potentially empty.
     */
    fun readByCriteria(criteria: ICriteria<T>): List<T>

    /**
     * Create the given entity in storage.
     *
     * Enforces entity uniquness on [entity]'s id property.
     *
     * @param[entity] An instance of [T] to create in storage.
     * @return The new entity in storage, or null if it already exists.
     */
    fun create(entity: T): T?

    /**
     * Update the given entity in storage.
     *
     * Uses [entity]'s id to fetch in storage.
     *
     * @param[entity] The entity to update in storage.
     * @return The new entity in storage, or null if it does not exist.
     */
    fun update(entity: T): T?

    /**
     * Delete the given entity in storage.
     *
     * Uses [entity]'s id to fetch in storage.
     *
     * @param[entity] The entity to delete in storage.
     * @return The entity that was deleted, or null if the entity does not exist.
     */
    fun delete(entity: T): T?
}