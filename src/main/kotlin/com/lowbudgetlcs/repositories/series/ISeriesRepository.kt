package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.models.Series
import com.lowbudgetlcs.models.SeriesId
import com.lowbudgetlcs.repositories.IRepository
import com.lowbudgetlcs.repositories.IUniqueRepository

interface ISeriesRepository : IUniqueRepository<Series, SeriesId>, IRepository<Series>