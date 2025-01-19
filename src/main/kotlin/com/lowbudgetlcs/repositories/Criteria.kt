package com.lowbudgetlcs.repositories

interface Criteria<T> {
    fun meetCriteria(entities: List<T>): List<T>
}