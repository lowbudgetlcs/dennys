package com.lowbudgetlcs.bridges

import com.sksamuel.hoplite.ConfigLoaderBuilder
import no.stelar7.api.r4j.basic.APICredentials
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard
import no.stelar7.api.r4j.impl.R4J
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch

data class RiotConfig(val apiKey: String)

class RiotBridge {
    private val config = ConfigLoaderBuilder.default().build().loadConfigOrThrow<RiotConfig>("/riot.yaml")
    private val riot by lazy { R4J(APICredentials(config.apiKey)) }
    fun match(id: Long): LOLMatch = riot.loLAPI.matchAPI.getMatch(RegionShard.AMERICAS, "NA1_$id")
}