package com.lowbudgetlcs.repositories.tokens

import com.lowbudgetlcs.domain.auth.models.AccessToken
import com.lowbudgetlcs.domain.auth.models.NewAccessToken
import com.lowbudgetlcs.domain.user.models.toUserId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.ACCESS_TOKENS

class AccessTokenRepository(
    private val dsl: DSLContext,
) : IAccessTokenRepository {
    override fun getByTokenHash(tokenHash: String): AccessToken? =
        selectAccessTokens().where(ACCESS_TOKENS.TOKEN_HASH.eq(tokenHash)).fetchOne(::rowToAccessToken)

    override fun insert(
        newToken: NewAccessToken,
        tokenHash: String,
    ): AccessToken? {
        val tokenHash =
            dsl
                .insertInto(
                    ACCESS_TOKENS,
                ).set(ACCESS_TOKENS.TOKEN_HASH, tokenHash)
                .set(ACCESS_TOKENS.NAME, newToken.name)
                .set(ACCESS_TOKENS.EXPIRES_AT, newToken.expiresAt)
                .set(ACCESS_TOKENS.SCOPES, newToken.scopes.joinToString(":"))
                .set(ACCESS_TOKENS.USER_ID, newToken.userId.value)
                .returning(ACCESS_TOKENS.TOKEN_HASH)
                .fetchOne()
                ?.get(ACCESS_TOKENS.TOKEN_HASH)
        return tokenHash?.let(::getByTokenHash)
    }

    private fun selectAccessTokens() =
        dsl
            .select(
                ACCESS_TOKENS.TOKEN_HASH,
                ACCESS_TOKENS.NAME,
                ACCESS_TOKENS.EXPIRES_AT,
                ACCESS_TOKENS.CREATED_AT,
                ACCESS_TOKENS.SCOPES,
                ACCESS_TOKENS.USER_ID,
            ).from(ACCESS_TOKENS)

    fun rowToAccessToken(row: Record): AccessToken? {
        val tokenHash = row[ACCESS_TOKENS.TOKEN_HASH] ?: return null
        val name = row[ACCESS_TOKENS.NAME] ?: return null
        val expiresAt = row[ACCESS_TOKENS.EXPIRES_AT] ?: return null
        val createdAt = row[ACCESS_TOKENS.CREATED_AT] ?: return null
        val scopes = row[ACCESS_TOKENS.SCOPES]?.split(":")?.toSet() ?: return null
        val userId = row[ACCESS_TOKENS.USER_ID]?.toUserId() ?: return null

        return AccessToken(
            tokenHash = tokenHash,
            name = name,
            expiresAt = expiresAt,
            createdAt = createdAt,
            scopes = scopes,
            userId = userId,
        )
    }
}
