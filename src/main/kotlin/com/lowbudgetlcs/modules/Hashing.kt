package com.lowbudgetlcs.modules

import com.lowbudgetlcs.hashing.Argon2Hasher
import com.lowbudgetlcs.hashing.IHasher
import com.lowbudgetlcs.hashing.Sha256Hasher
import org.koin.core.qualifier.named
import org.koin.dsl.module

val hashingModule =
    module {
        single<IHasher>(named("argon2")) { Argon2Hasher() }
        single<IHasher>(named("sha256")) { Sha256Hasher() }
    }
