package com.lowbudgetlcs.dto.lol.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    @SerialName("metadata") val metaData: MatchMetadataDto,
    @SerialName("info") val matchInfo: MatchInfoDto
)
