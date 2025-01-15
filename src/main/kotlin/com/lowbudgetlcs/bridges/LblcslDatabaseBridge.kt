package com.lowbudgetlcs.bridges

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.lowbudgetlcs.Database
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant

data class DatabaseConfig(val lblcs: Lblcs)
data class Lblcs(val url: Masked, val pass: Masked)

object LblcsDatabaseBridge {
    @OptIn(ExperimentalHoplite::class)
    private val config =
        ConfigLoaderBuilder.default().withExplicitSealedTypes().addResourceSource("/database.yaml").build()
            .loadConfigOrThrow<DatabaseConfig>()

    private val driver by lazy {
        HikariConfig().apply {
            jdbcUrl = config.lblcs.url.value
            password = config.lblcs.pass.value
        }.let {
            HikariDataSource(it).asJdbcDriver()
        }
    }
    val db by lazy {
        Database(driver)
    }

    // Given a list of MatchParticipants, fetch the team id from the database
    // I am nearly positive there is a way to do this in a single query rather than (potentially) 5.
    private fun getTeamId(players: List<MatchParticipant>): Int {
        for (player in players) {
            lblcs.playersQueries.selectTeamIdByPuuid(player.puuid).executeAsOneOrNull()?.let {
                it.team_id?.let { teamId ->
                    return teamId
                }
            }
        }
        logger.warn("No players in match exist in database.")
        return -1
    }
}