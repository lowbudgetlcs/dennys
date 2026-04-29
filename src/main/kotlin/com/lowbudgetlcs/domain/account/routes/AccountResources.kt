package com.lowbudgetlcs.domain.account.routes

import io.ktor.resources.Resource

@Resource("/")
class AccountResources {
    @Resource("{accountId}")
    data class ById(
        val parent: AccountResources = AccountResources(),
        val accountId: Int,
    )
}
