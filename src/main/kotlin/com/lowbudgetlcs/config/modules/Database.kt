package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.config.createDslContext
import org.jooq.DSLContext
import org.koin.dsl.module

val databaseModule =
    module {
        single<DSLContext> { createDslContext(get()) }
    }
