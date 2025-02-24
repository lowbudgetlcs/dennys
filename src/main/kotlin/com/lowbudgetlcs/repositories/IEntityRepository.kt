package com.lowbudgetlcs.repositories

/**
 * A data-source agnostic contract for [IRepository]'s containing objects
 * with unique ids of type [K].
 */
interface IEntityRepository<T, K> : IRepository<T> {
    /**
     * Returns an entity with [id] from storage, null otherwise.
     */
    fun readById(id: K): T?
}