package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.repositories.Criteria
import migrations.Players


class PlayerRepositoryImpl : PlayerRepository {

    private val lblcs = LblcsDatabaseBridge().db

    override fun readAll(): List<Player> = lblcs.playersQueries.readAll().executeAsList().map { transform(it) }

    override fun readById(id: PlayerId): Player? =
        lblcs.playersQueries.readById(id.id).executeAsOneOrNull()?.let { transform(it) }

    override fun readByCriteria(criteria: Criteria<Player>): List<Player> {
        TODO("Not yet implemented")
    }

    override fun create(entity: Player): Player {
        TODO("Not yet implemented")
    }

    override fun update(entity: Player): Player =
        lblcs.playersQueries.updatePlayer(entity.summonerName, entity.id.id).executeAsOne().let { transform(it) }

    override fun delete(entity: Player): Player {
        TODO("Not yet implemented")
    }

    override fun readByPuuid(puuid: String): Player? =
        lblcs.playersQueries.selectByPuuid(puuid).executeAsOneOrNull()?.let { transform(it) }

    private fun readPlayerGameData(playerId: PlayerId): List<PlayerGameData> =
        lblcs.playerDataQueries.readByPlayerId(playerId.id).executeAsList().let { data ->
            data.map {
                PlayerGameData(
                    it.kills,
                    it.deaths,
                    it.assists,
                    it.level,
                    it.gold,
                    it.vision_score,
                    it.damage,
                    it.healing,
                    it.shielding,
                    it.damage_taken,
                    it.self_mitigated_damage,
                    it.damage_to_turrets,
                    it.longest_life,
                    it.double_kills,
                    it.triple_kills,
                    it.quadra_kills,
                    it.penta_kills,
                    it.cs,
                    it.champion_name,
                    it.item0,
                    it.item1,
                    it.item2,
                    it.item3,
                    it.item4,
                    it.item5,
                    it.trinket,
                    it.keystone_rune,
                    it.secondary_tree,
                    it.summoner1,
                    it.summoner2
                )
            }
        }

    override fun createPlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player {
        lblcs.transaction {
            createPlayerGameData(
                createPerformance(player, game), data
            )
        }
        val gameData by lazy {
            readPlayerGameData(player.id)
        }
        return player.copy(gameData = gameData)
    }

    private fun createPerformance(player: Player, game: Game): PlayerPerformanceId =
        lblcs.playerPerformancesQueries.createPerformance(player.puuid, game.id.id).executeAsOne().let {
            PlayerPerformanceId(it)
        }

    private fun createPlayerGameData(
        performance: PlayerPerformanceId, data: PlayerGameData
    ) = lblcs.playerDataQueries.createPlayerData(
        performance.id,
        data.kills,
        data.deaths,
        data.assists,
        data.championLevel,
        data.goldEarned,
        data.visionScore,
        data.totalDamageToChampions,
        data.totalHealsOnTeammates,
        data.totalDamageShieldedOnTeammates,
        data.totalDamageTaken,
        data.damageSelfMitigated,
        data.damageDealtToTurrets,
        data.longestTimeSpentLiving,
        data.doubleKills,
        data.tripleKills,
        data.quadraKills,
        data.pentaKills,
        data.cs,
        data.championName,
        data.item0,
        data.item1,
        data.item2,
        data.item3,
        data.item4,
        data.item5,
        data.item6,
        data.keystone,
        data.secondaryKeystone,
        data.summoner1,
        data.summoner2
    ).executeAsOne()

    private fun transform(entity: Players): Player {
        val gameData by lazy {
            readPlayerGameData(PlayerId(entity.id))
        }
        return Player(
            PlayerId(entity.id),
            entity.summoner_name,
            entity.riot_puuid,
            entity.team_id?.let { TeamId(it) },
            gameData,
        )
    }
}