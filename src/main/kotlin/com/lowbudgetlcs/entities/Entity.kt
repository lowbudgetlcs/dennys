package com.lowbudgetlcs.entities

/**
 * A generic entity supporting keys of type [K].
 *
 * @param [K] The unique id property of an [Entity]
 */
interface Entity<K> {
    val id: K
}