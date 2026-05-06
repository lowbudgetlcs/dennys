package com.lowbudgetlcs.domain

class RepositoryException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
