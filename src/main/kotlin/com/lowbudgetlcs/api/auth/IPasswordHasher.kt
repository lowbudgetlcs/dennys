package com.lowbudgetlcs.api.auth

interface IPasswordHasher {
    fun hash(input: String): String

    fun verify(
        input: String,
        expectedHash: String,
    ): Boolean
}
