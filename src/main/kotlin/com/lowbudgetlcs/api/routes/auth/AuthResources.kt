package com.lowbudgetlcs.api.routes.auth

import io.ktor.resources.Resource

@Resource("/")
class AuthResources {
    @Resource("/user")
    data class User(
        val parent: AuthResources = AuthResources(),
    )
}
