package com.lowbudgetlcs.repositories.template

data class Template(val id: TemplateId, val text: String, val size: Long)

data class TemplateId(val id: Int)
