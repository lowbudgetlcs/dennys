package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.InvalidRequestException

data class StatsSort(
    val stat: String,
    val descending: Boolean = true,
) {
    companion object {
        /** Parses `stat` or `stat:asc|desc`; the direction defaults to descending. */
        fun parse(raw: String): StatsSort {
            val parts = raw.split(":")
            val stat = parts[0]
            if (stat.isBlank() ||
                parts.size > 2
            ) {
                throw InvalidRequestException("sort must be '<stat>' or '<stat>:asc|desc'.")
            }
            return when (parts.getOrNull(1)) {
                null, "desc" -> StatsSort(stat, descending = true)
                "asc" -> StatsSort(stat, descending = false)
                else -> throw InvalidRequestException("sort direction must be 'asc' or 'desc'.")
            }
        }
    }
}
