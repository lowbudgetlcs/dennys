package com.lowbudgetlcs.domain.auth.core

class UnauthorizedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
