package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.repositories.ICriteria

class SizeLargerThan30Criteria : ICriteria<Template> {
    override fun meetCriteria(entities: List<Template>): List<Template> = entities.filter {it.size > 30}
}