package com.lowbudgetlcs.hashing

import com.lowbudgetlcs.byteify
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

private const val HASH_MAX_TIME = 1000L
private const val HASH_MAX_MEMORY = 65550
private const val HASH_PARALLELISM = 2


class Argon2Hasher : IHasher {
    private val iterations: Deferred<Int> =
        CoroutineScope(Dispatchers.IO).async {
            Argon2Helper.findIterations(argon2, HASH_MAX_TIME, HASH_MAX_MEMORY, HASH_PARALLELISM)
        }


    override suspend fun hash(input: String): String =
        argon2.hash(iterations.await(), HASH_MAX_MEMORY, HASH_PARALLELISM, input.byteify())

    override suspend fun verify(
        input: String,
        expectedHash: String,
    ): Boolean {
        val b = input.byteify()
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
