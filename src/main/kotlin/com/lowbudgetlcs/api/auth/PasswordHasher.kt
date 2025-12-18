package com.lowbudgetlcs.api.auth

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper

class PasswordHasher : IPasswordHasher {
    companion object {
        private val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        // TODO: Add these params to default.properties
        private val iterations = Argon2Helper.findIterations(argon2, 1000, 65550, 1)
    }

    private fun byteify(s: String): ByteArray = s.toByteArray(Charsets.UTF_8)

    override fun hash(input: String): String = argon2.hash(iterations, 65550, 4, byteify(input))

    override fun verify(
        input: String,
        expectedHash: String,
    ): Boolean {
        val b = byteify(input)
        try {
            return argon2.verify(expectedHash, b)
        } finally {
            argon2.wipeArray(b)
        }
    }
}
