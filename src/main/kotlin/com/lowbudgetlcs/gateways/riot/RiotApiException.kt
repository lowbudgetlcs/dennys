package com.lowbudgetlcs.gateways.riot

class RiotApiException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
