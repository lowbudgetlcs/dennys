package com.lowbudgetlcs.repositories.divisions

import com.lowbudgetlcs.models.Division
import com.lowbudgetlcs.models.DivisionId
import com.lowbudgetlcs.repositories.ICriteria

class AllDivisionsDatabase : IDivisionRepository {
    override fun save(entity: Division): Division? {
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

    override fun update(entity: Division): Division? {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Division): Division? {
        TODO("Not yet implemented")
    }
}