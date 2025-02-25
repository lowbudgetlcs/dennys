package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.repositories.ICriteria

/**
 * This is an example of an In-Memory repository. Many of the concepts apply across
 * all possible data sources.
 */
class AllTemplatesInMemory : ITemplateRepository {
    /**
     * In-memory storage.
     */
    private val templates: MutableMap<TemplateId, Template> = mutableMapOf()
    override fun readAll(): List<Template> = templates.values.toList()
    override fun readByText(text: String): List<Template> = templates.values.filter { it.text.contains(text) }

    override fun readById(id: TemplateId): Template? = templates[id]

    override fun readByCriteria(criteria: ICriteria<Template>): List<Template> =
        criteria.meetCriteria(templates.values.toList())

    override fun save(entity: Template): Template? {
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