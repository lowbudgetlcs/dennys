package com.lowbudgetlcs.domain.auth.models

import com.lowbudgetlcs.domain.auth.models.types.SessionId
import com.sksamuel.hoplite.Masked
import java.util.UUID

// Type Extensions
fun String.toMasked(): Masked = Masked(this)

fun UUID.toSessionId(): SessionId = SessionId(this)
