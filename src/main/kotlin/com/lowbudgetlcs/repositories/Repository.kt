package com.lowbudgetlcs.repositories

interface Repository<T, K> {
    fun readAll(): List<T>
    fun readById(id: K): T?
    fun readByCriteria(criteria: Criteria<T>): List<T>
    fun create(entity: T): T
    fun update(entity: T): T
    fun delete(entity: T): T
}