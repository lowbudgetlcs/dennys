package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.config.*
import org.koin.dsl.module

val configModule =
    module {
        single<RiotConfig> { appConfig.riot }
        single<DatabaseConfig> { appConfig.database }
        single<CookieConfig> { appConfig.cookie }
        single<CorsConfig> { appConfig.cors}
        single<AppConfig> { appConfig }
    }
