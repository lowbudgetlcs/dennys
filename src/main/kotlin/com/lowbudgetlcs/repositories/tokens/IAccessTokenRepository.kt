package com.lowbudgetlcs.repositories.tokens

import com.lowbudgetlcs.domain.auth.models.AccessToken
import com.lowbudgetlcs.domain.auth.models.NewAccessToken

interface IAccessTokenRepository {
    fun getByTokenHash(tokenHash: String): AccessToken?

    fun insert(
        newToken: NewAccessToken,
        tokenHash: String,
    ): AccessToken?
}
