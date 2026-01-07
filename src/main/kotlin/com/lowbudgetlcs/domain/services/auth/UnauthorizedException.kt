package com.lowbudgetlcs.domain.services.auth

class UnauthorizedException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
