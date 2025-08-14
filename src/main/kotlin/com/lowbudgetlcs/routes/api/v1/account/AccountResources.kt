package com.lowbudgetlcs.routes.api.v1.account

import io.ktor.resources.*

@Resource("/")
class AccountResources {
    @Resource("{accountId}")
    data class ById(
        val parent: AccountResources = AccountResources(), val accountId: Int
    )
}