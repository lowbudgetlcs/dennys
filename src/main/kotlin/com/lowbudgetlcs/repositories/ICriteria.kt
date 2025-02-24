package com.lowbudgetlcs.repositories

/**
 * A composable filter compatible with [IRepository]s.
 */
interface ICriteria<T> {
    fun meetCriteria(entities: List<T>): List<T>
}