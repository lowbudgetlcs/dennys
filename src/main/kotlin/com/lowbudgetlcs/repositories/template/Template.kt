package com.lowbudgetlcs.repositories.template

import com.lowbudgetlcs.entities.Entity

data class Template(override val id: TemplateKey, val size: Long) : Entity<TemplateKey>

data class TemplateKey(val id: Int)
