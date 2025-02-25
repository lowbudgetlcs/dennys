package com.lowbudgetlcs.entities

/**
 * A generic entity contract with ids of type [K]. [id] is unique.
 */
interface Entity<K> {
    val id: K
}