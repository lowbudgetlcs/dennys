package com.lowbudgetlcs.api.routes.v1.account

import io.ktor.resources.Resource

@Resource("/")
class AccountResourcesV1 {
    @Resource("{accountId}")
    data class ById(
        val parent: AccountResourcesV1 = AccountResourcesV1(),
        val accountId: Int,
    )
}
