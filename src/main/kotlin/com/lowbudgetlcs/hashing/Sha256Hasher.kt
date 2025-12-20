package com.lowbudgetlcs.hashing

import java.security.MessageDigest

class Sha256Hasher : IHasher {
    override fun hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { String.format("%02x", it) } // Convert bytes to hex string
    }

    override fun verify(
        input: String,
        expectedHash: String,
    ): Boolean = hash(input) == expectedHash
}
