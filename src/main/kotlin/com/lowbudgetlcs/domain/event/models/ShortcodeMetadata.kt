package com.lowbudgetlcs.domain.event.models

import kotlinx.serialization.Serializable

@Serializable
data class ShortcodeMetadata(val tag: String = "LBLCS")
