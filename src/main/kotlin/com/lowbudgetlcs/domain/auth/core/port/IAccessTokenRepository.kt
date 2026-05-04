package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.auth.core.models.AccessToken
import com.lowbudgetlcs.domain.auth.core.models.NewAccessToken

interface IAccessTokenRepository {
    suspend fun getByTokenHash(tokenHash: String): AccessToken?
    suspend fun insert(
        newToken: NewAccessToken,
        tokenHash: String,
    ): AccessToken?
}
