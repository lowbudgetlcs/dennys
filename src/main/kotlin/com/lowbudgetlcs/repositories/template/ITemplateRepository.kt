package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.repositories.IRepository
import com.lowbudgetlcs.repositories.IUniqueRepository

interface ITemplateRepository : IUniqueRepository<Template, TemplateId>, IRepository<Template> {
    /**
     * Returns a list of [Template]s that contain [text] in it's [Template.text] field.
     */
    fun readByText(text: String): List<Template>
}