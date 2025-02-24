package com.lowbudgetlcs.repositories

/**
 * A composable filter compatible with [IRepository]s.
 *
 * @param[T] The type of objects to be filtered.
 * @return A filtered list of objects of type [T].
 */
interface ICriteria<T> {
    fun meetCriteria(entities: List<T>): List<T>
}