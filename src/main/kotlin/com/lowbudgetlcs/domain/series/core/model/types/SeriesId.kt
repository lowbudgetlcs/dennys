package com.lowbudgetlcs.domain.series.core.model.types

@JvmInline
value class SeriesId(
    val value: Int,
)

// Extensions
fun Int.toSeriesId(): SeriesId = SeriesId(this)

