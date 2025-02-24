package com.lowbudgetlcs.repositories

class AndCriteria<T>(private val criteria1: ICriteria<T>, private val criteria2: ICriteria<T>) : ICriteria<T> {
    /**
     * Compose two [ICriteria] with an AND operation.
     *
     * @param[T] The type of objects to be filtered.
     *
     * @return A list of objects with
     */
    override fun meetCriteria(entities: List<T>): List<T> = criteria2.meetCriteria(criteria1.meetCriteria(entities))
}