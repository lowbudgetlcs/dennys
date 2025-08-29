package com.lowbudgetlcs.api.dto.riot.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    @SerialName("metadata") val metaData: MatchMetadataDto,
    @SerialName("info") val matchInfo: MatchInfoDto
)
