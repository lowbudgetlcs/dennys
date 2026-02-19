package com.lowbudgetlcs.domain.auth

class UnauthorizedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
