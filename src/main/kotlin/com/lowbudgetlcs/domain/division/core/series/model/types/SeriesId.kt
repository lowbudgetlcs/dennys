package com.lowbudgetlcs.domain.division.core.series.model.types

@JvmInline
value class SeriesId(
    val value: Int,
)

// Extensions
fun Int.toSeriesId(): SeriesId = SeriesId(this)

