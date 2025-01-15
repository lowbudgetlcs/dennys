package com.lowbudgetlcs.repositories.series

interface SeriesRepository {
    fun updateWinnerLoserById(winnerId: Int, loserId: Int, seriesId: Int): Boolean
}