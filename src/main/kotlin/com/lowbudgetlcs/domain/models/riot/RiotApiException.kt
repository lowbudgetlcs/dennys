package com.lowbudgetlcs.domain.models.riot

class RiotApiException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
