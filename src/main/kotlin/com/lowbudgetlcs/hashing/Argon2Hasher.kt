package com.lowbudgetlcs.hashing

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Argon2Hasher : IHasher {
    // TODO: Add these params to default.properties
    private val iterations: Deferred<Int> =
        CoroutineScope(Dispatchers.IO).async {
            Argon2Helper.findIterations(argon2, 1000, 65550, 1)
        }

    private fun byteify(s: String): ByteArray = s.toByteArray(Charsets.UTF_8)

    override suspend fun hash(input: String): String = argon2.hash(iterations.await(), 65550, 4, byteify(input))

    override suspend fun verify(
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

    companion object {
        private val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
    }
}
