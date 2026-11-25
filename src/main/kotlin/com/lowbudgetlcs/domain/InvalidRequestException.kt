package com.lowbudgetlcs.domain

/** A 400: the request is well-formed but asks for something the API doesn't offer, such as an unknown sort. */
class InvalidRequestException(
    message: String,
) : IllegalArgumentException(message)
