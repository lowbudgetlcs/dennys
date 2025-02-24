package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.entities.Entity

data class Template(override val id: TemplateId, val text: String, val size: Long) : Entity<TemplateId>

data class TemplateId(val id: Int)
