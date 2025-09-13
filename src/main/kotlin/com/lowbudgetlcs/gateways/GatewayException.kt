package com.lowbudgetlcs.gateways

class GatewayException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
