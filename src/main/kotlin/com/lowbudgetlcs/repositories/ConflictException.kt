package com.lowbudgetlcs.repositories

/** A 409 whose [reason] lets a client tell this conflict from any other. */
class ConflictException(
    message: String,
    val reason: String,
) : IllegalStateException(message)
