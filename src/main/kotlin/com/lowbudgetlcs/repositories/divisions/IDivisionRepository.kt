package com.lowbudgetlcs.repositories.divisions

import com.lowbudgetlcs.models.Division
import com.lowbudgetlcs.models.DivisionId
import com.lowbudgetlcs.repositories.IRepository
import com.lowbudgetlcs.repositories.IUniqueRepository

interface IDivisionRepository : IUniqueRepository<Division, DivisionId>, IRepository<Division>