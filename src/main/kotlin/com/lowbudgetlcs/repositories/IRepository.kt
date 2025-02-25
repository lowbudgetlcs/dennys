package com.lowbudgetlcs.repositories

/**
 * A data-source agnostic contract for repositories containing
 * objects of type [T].
 */
interface IRepository<T> {
    /**
     * Returns all objects from storage.
     */
    fun readAll(): List<T>

    /**
     * Returns all objects matching [criteria] from storage.
     */
    fun readByCriteria(criteria: ICriteria<T>): List<T>

    /**
     * Save [entity] in storage. Returns [entity] if successful, null otherwise.
     */
    fun save(entity: T): T?

    /**
     * Update [entity] in storage. Returns [entity] if successful, null otherwise.
     */
    fun update(entity: T): T?

    /**
     * Deletes [entity] from storage. Returns [entity] if successful, null otherwise
     * (notably if [entity] doesn't exist in storage.).
     */
    fun delete(entity: T): T?
}