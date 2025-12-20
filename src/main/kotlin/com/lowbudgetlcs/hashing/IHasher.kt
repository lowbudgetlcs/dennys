package com.lowbudgetlcs.hashing

interface IHasher {
    fun hash(input: String): String

    fun verify(
        input: String,
        expectedHash: String,
    ): Boolean
}
