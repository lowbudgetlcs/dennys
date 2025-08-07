package com.lowbudgetlcs.routes.api.v1.account

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/account")
class AccountRoutes {

    @Serializable
    @Resource("{accountId}")
    data class ById(
        val parent: AccountRoutes = AccountRoutes(),
        val accountId: Int
    )
}