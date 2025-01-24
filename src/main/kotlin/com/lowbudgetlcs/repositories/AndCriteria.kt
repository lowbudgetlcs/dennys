package com.lowbudgetlcs.repositories

class AndCriteria<T>(private val criteria1: Criteria<T>, private val criteria2: Criteria<T>) : Criteria<T> {

    override fun meetCriteria(entities: List<T>): List<T> = criteria2.meetCriteria(criteria1.meetCriteria(entities))
}