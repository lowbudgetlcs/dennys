package com.lowbudgetlcs.routes.api.v1.account

import io.ktor.resources.*

@Resource("/")
class AccountResourcesV1 {
    @Resource("{accountId}")
    data class ById(
        val parent: AccountResourcesV1 = AccountResourcesV1(), val accountId: Int
    )
}