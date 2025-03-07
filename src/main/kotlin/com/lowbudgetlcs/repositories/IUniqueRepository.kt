package com.lowbudgetlcs.repositories

/**
 * A data-source agnostic contract for [IRepository]'s containing objects
 * with unique ids of type [K].
 */
interface IUniqueRepository<T, K> {
    /**
     * Returns an entity with [id] from storage, null otherwise.
     */
    fun readById(id: K): T?
}