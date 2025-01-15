package com.lowbudgetlcs.bridges

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.logging.*
import no.stelar7.api.r4j.basic.APICredentials
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard
import no.stelar7.api.r4j.impl.R4J
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch

data class RiotConfig(val apiKey: String, val useStub: Boolean)

class RiotBridge {
    companion object {
        private val logger = KtorSimpleLogger("com.lowbudgetlcs.RiotBridge")

        @OptIn(ExperimentalHoplite::class)
        private val config =
            ConfigLoaderBuilder.default().withExplicitSealedTypes().addResourceSource("/riot.yaml").build()
                .loadConfigOrThrow<RiotConfig>()
    }

    private val riot by lazy { R4J(APICredentials(config.apiKey)) }
    fun match(gameId: Long): LOLMatch? = try {
        riot.loLAPI.matchAPI.getMatch(RegionShard.AMERICAS, "NA1_$gameId")
    } catch (e: Throwable) {
        logger.error("Error while retrieving match '{}'", gameId)
        logger.error(e)
        null
    }

    fun fetchTeams(gameId: Long) = riot.loLAPI.getTournamentAPI(config.useStub)
}