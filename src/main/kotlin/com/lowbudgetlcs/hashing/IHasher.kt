package com.lowbudgetlcs.hashing

interface IHasher {
    suspend fun hash(input: String): String

    suspend fun verify(
        input: String,
        expectedHash: String,
    ): Boolean
}
