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

    private fun readPlayerPerformances(playerId: PlayerId): List<PlayerPerformance> =
        lblcs.playerPerformancesQueries.readByPlayerId(playerId.id).executeAsList().let { perf ->
            perf.map {
                PlayerPerformance(
                    GameId(it.game_id), TeamId(it.team_id), DivisionId(it.division_id)
                )
            }
        }

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
        player: Player,
        game: Game,
        kills: Int,
        deaths: Int,
        assists: Int,
        championLevel: Int,
        goldEarned: Long,
        visionScore: Long,
        totalDamageToChampions: Long,
        totalHealsOnTeammates: Long,
        totalDamageShieldedOnTeammates: Long,
        totalDamageTaken: Long,
        damageSelfMitigated: Long,
        damageDealtToTurrets: Long,
        longestTimeSpentLiving: Long,
        doubleKills: Short,
        tripleKills: Short,
        quadraKills: Short,
        pentaKills: Short,
        cs: Int,
        championName: String,
        item0: Int,
        item1: Int,
        item2: Int,
        item3: Int,
        item4: Int,
        item5: Int,
        item6: Int,
        keystone: Int,
        secondaryKeystone: Int,
        summoner1: Int,
        summoner2: Int
    ): Player {
        lblcs.transaction {
            createPlayerGameData(
                player,
                createPerformance(player, game),
                kills,
                deaths,
                assists,
                championLevel,
                goldEarned,
                visionScore,
                totalDamageToChampions,
                totalHealsOnTeammates,
                totalDamageShieldedOnTeammates,
                totalDamageTaken,
                damageSelfMitigated,
                damageDealtToTurrets,
                longestTimeSpentLiving,
                doubleKills,
                tripleKills,
                quadraKills,
                pentaKills,
                cs,
                championName,
                item0,
                item1,
                item2,
                item3,
                item4,
                item5,
                item6,
                keystone,
                secondaryKeystone,
                summoner1,
                summoner2
            )
        }
        val performances by lazy {
            readPlayerPerformances(player.id)
        }
        val gameData by lazy {
            readPlayerGameData(player.id)
        }
        return player.copy(performances = performances, gameData = gameData)
    }

    private fun createPerformance(player: Player, game: Game): PlayerPerformanceId =
        lblcs.playerPerformancesQueries.createPerformance(player.puuid, game.id.id).executeAsOne().let {
            PlayerPerformanceId(it)
        }

    private fun createPlayerGameData(
        player: Player,
        performance: PlayerPerformanceId,
        kills: Int,
        deaths: Int,
        assists: Int,
        championLevel: Int,
        goldEarned: Long,
        visionScore: Long,
        totalDamageToChampions: Long,
        totalHealsOnTeammates: Long,
        totalDamageShieldedOnTeammates: Long,
        totalDamageTaken: Long,
        damageSelfMitigated: Long,
        damageDealtToTurrets: Long,
        longestTimeSpentLiving: Long,
        doubleKills: Short,
        tripleKills: Short,
        quadraKills: Short,
        pentaKills: Short,
        cs: Int,
        championName: String,
        item0: Int,
        item1: Int,
        item2: Int,
        item3: Int,
        item4: Int,
        item5: Int,
        item6: Int,
        keystone: Int,
        secondaryKeystone: Int,
        summoner1: Int,
        summoner2: Int
    ): PlayerGameDataId = lblcs.playerDataQueries.createPlayerData(
        performance.id,
        kills,
        deaths,
        assists,
        championLevel,
        goldEarned,
        visionScore,
        totalDamageToChampions,
        totalHealsOnTeammates,
        totalDamageShieldedOnTeammates,
        totalDamageTaken,
        damageSelfMitigated,
        damageDealtToTurrets,
        longestTimeSpentLiving,
        doubleKills,
        tripleKills,
        quadraKills,
        pentaKills,
        cs,
        championName,
        item0,
        item1,
        item2,
        item3,
        item4,
        item5,
        item6,
        keystone,
        secondaryKeystone,
        summoner1,
        summoner2
    ).executeAsOne().let {
        PlayerGameDataId(it)
    }

    private fun transform(entity: Players): Player {
        val performances by lazy {
            readPlayerPerformances(PlayerId(entity.id))
        }
        val gameData by lazy {
            readPlayerGameData(PlayerId(entity.id))
        }
        return Player(
            PlayerId(entity.id),
            entity.summoner_name,
            entity.riot_puuid,
            entity.team_id?.let { TeamId(it) },
            performances,
            gameData,
        )
    }
}