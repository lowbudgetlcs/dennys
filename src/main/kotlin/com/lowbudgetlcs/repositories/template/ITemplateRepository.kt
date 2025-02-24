package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.repositories.IEntityRepository

interface ITemplateRepository : IEntityRepository<Template, TemplateId> {
    /**
     * Returns a list of [Template]s that contain [text] in it's [Template.text] field.
     */
    fun readByText(text: String): List<Template>
}