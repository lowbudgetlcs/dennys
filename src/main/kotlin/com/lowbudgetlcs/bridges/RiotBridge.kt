package com.lowbudgetlcs.bridges

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import no.stelar7.api.r4j.basic.APICredentials
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard
import no.stelar7.api.r4j.impl.R4J
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class RiotConfig(val apiKey: String, val useStub: Boolean)

/**
 * Wrapper for configuring and accessing an [R4J] instance.
 */
class RiotBridge {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RiotBridge::class.java)

        @OptIn(ExperimentalHoplite::class)
        private val config =
            ConfigLoaderBuilder.default().withExplicitSealedTypes().addResourceSource("/riot.yaml").build()
                .loadConfigOrThrow<RiotConfig>()
    }

    private val riot by lazy { R4J(APICredentials(config.apiKey)) }

    /**
     * Fetch a [LOLMatch] from the RiotAPI.
     *
     * @param[gameId] matchId to send to Riot.
     * @return An instance of [LOLMatch] if [gameId] exists, null otherwise.
     */
    fun match(gameId: Long): LOLMatch? = try {
        logger.info("🔍 Fetching match data for game ID: $gameId")

        val match = riot.loLAPI.matchAPI.getMatch(RegionShard.AMERICAS, "NA1_$gameId")
        logger.info("✅ Successfully retrieved match data for game ID: $gameId")
        match
    } catch (e: Throwable) {
        logger.error("❌ Error while retrieving match '$gameId'", e)
        null
    }
}