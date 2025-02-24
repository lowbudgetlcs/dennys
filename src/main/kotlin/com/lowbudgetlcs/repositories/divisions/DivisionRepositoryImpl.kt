package com.lowbudgetlcs.repositories.divisions

import com.lowbudgetlcs.entities.Division
import com.lowbudgetlcs.entities.DivisionId
import com.lowbudgetlcs.repositories.ICriteria
import com.lowbudgetlcs.repositories.IRepository

class DivisionRepositoryImpl : IDivisionRepository, IRepository<Division, DivisionId> {
    override fun create(entity: Division): Division {
        TODO("Not yet implemented")
    }

    override fun readAll(): List<Division> {
        TODO("Not yet implemented")
    }

    override fun readById(id: DivisionId): Division? {
        TODO("Not yet implemented")
    }

    override fun readByCriteria(criteria: ICriteria<Division>): List<Division> {
        TODO("Not yet implemented")
    }

    override fun update(entity: Division): Division {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Division): Division {
        TODO("Not yet implemented")
    }
}