package com.lowbudgetlcs.modules

import com.lowbudgetlcs.createDslContext
import org.jooq.DSLContext
import org.koin.dsl.module

val databaseModule =
    module {
        single<DSLContext> { createDslContext(get()) }
    }
