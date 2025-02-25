package com.lowbudgetlcs.repositories

/**
 * Returns objects matching [criteria1] AND [criteria2].
 */
class AndCriteria<T>(private val criteria1: ICriteria<T>, private val criteria2: ICriteria<T>) : ICriteria<T> {
    override fun meetCriteria(entities: List<T>): List<T> = criteria2.meetCriteria(criteria1.meetCriteria(entities))
}