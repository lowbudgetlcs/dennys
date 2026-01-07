package com.lowbudgetlcs.modules

import com.lowbudgetlcs.config.AppConfig
import com.lowbudgetlcs.config.CookieConfig
import com.lowbudgetlcs.config.DatabaseConfig
import com.lowbudgetlcs.config.RiotConfig
import com.lowbudgetlcs.config.appConfig
import org.koin.dsl.module

val configModule =
    module {
        single<RiotConfig> { appConfig.riot }
        single<DatabaseConfig> { appConfig.database }
        single<CookieConfig> { appConfig.cookie }
        single<AppConfig> { appConfig }
    }
