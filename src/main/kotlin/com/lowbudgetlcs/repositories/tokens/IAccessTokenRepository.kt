package com.lowbudgetlcs.repositories.tokens

import com.lowbudgetlcs.domain.models.auth.AccessToken
import com.lowbudgetlcs.domain.models.auth.NewAccessToken

interface IAccessTokenRepository {
    fun getByTokenHash(tokenHash: String): AccessToken?

    fun insert(
        newToken: NewAccessToken,
        tokenHash: String,
    ): AccessToken?
}
