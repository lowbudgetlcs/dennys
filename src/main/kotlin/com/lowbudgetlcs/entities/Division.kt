package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable

/**
 * Represents a Division, of the given [id] and [name].
 * @property tournamentId The Riot-supplied tournament id associated with a [Division]. This is generated
 * whenever a [Division] is created, and is generally immutable.
 */
@Serializable
data class Division(override val id: DivisionId, val name: String, val tournamentId: Int) : Entity<DivisionId>

/**
 * ID type for [Division]s.
 */
@Serializable
data class DivisionId(val id: Int)
