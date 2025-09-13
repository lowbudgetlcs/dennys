package com.lowbudgetlcs.repositories

class DatabaseException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
