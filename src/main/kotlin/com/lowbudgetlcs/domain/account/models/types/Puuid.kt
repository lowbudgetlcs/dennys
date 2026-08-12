package com.lowbudgetlcs.domain.account.models.types

const val PUUID_LENGTH = 78

/**
 * Riot PUUIDs are base64url, so the charset is checked as well as the length. This is a security
 * boundary, not just tidiness: the value reaches a Riot URL as a path segment, and a caller-supplied
 * value carrying '/' or '..' could otherwise redirect that request to another path on Riot's host
 * with our API key attached.
 */
private val PUUID_CHARSET = Regex("[A-Za-z0-9_-]*")

@JvmInline
value class Puuid(
    val value: String,
) {
    init {
        require(value.length == PUUID_LENGTH) { "Puuids must be exactly $PUUID_LENGTH characters." }
        require(PUUID_CHARSET.matches(value)) {
            "Puuids must contain only letters, digits, '-' and '_'."
        }
    }
}
