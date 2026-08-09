package com.lowbudgetlcs.gateways.riot

class RiotApiException(
    message: String,
    val status: Int? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    val retryable: Boolean get() = status == null || status == TOO_MANY_REQUESTS || status >= SERVER_ERROR

    private companion object {
        const val TOO_MANY_REQUESTS = 429
        const val SERVER_ERROR = 500
    }
}
