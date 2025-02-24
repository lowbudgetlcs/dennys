package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.repositories.ICriteria

class AllTemplatesInMemory : ITemplateRepository {
    private val templates: MutableMap<TemplateKey, Template> = mutableMapOf()
    override fun readAll(): List<Template> = templates.values.toList()

    override fun readById(id: TemplateKey): Template? = templates[id]

    override fun readByCriteria(criteria: ICriteria<Template>): List<Template> =
        criteria.meetCriteria(templates.values.toList())

    override fun create(entity: Template): Template? {
        if (templates[entity.id] != null) return null
        templates[entity.id] = entity
        return entity
    }

    override fun update(entity: Template): Template? {
        if (templates[entity.id] == null) return null
        templates[entity.id] = entity
        return entity
    }

    override fun delete(entity: Template): Template? = templates.remove(entity.id)
}