package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.entities.*
import com.lowbudgetlcs.repositories.ICriteria
import migrations.Player_game_data
import migrations.Players
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant


class AllPlayersLBLCS : IPlayerRepository {
    private val lblcs = LblcsDatabaseBridge().db

    override fun save(entity: Player): Player? {
        TODO("Not yet implemented")
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

    override fun readAll(): List<Player> = lblcs.playersQueries.readAll().executeAsList().map { it.toPlayer() }

    override fun readById(id: PlayerId): Player? = lblcs.playersQueries.readById(id.id).executeAsOneOrNull()?.toPlayer()

    override fun readByCriteria(criteria: ICriteria<Player>): List<Player> {
        TODO("Not yet implemented")
    }

    override fun readByPuuid(puuid: String): Player? =
        lblcs.playersQueries.readByPuuid(puuid).executeAsOneOrNull()?.toPlayer()

    override fun update(entity: Player): Player? =
        lblcs.playersQueries.updatePlayer(entity.summonerName, entity.id.id).executeAsOneOrNull()?.toPlayer()

    override fun delete(entity: Player): Player? {
        TODO("Not yet implemented")
    }

    override fun fetchTeamId(participants: List<MatchParticipant>): TeamId? {
        for (participant in participants) {
            this.readByPuuid(participant.puuid)?.let { player ->
                if (player.team != null) return player.team
            }
        }
        return null
    }

    private fun createPerformance(player: Player, game: Game): PlayerPerformanceId =
        lblcs.playersQueries.createPerformance(player.puuid, game.id.id).executeAsOne().let {
            PlayerPerformanceId(it)
        }

    private fun createPlayerGameData(
        performance: PlayerPerformanceId, data: PlayerGameData
    ) = lblcs.playersQueries.createPlayerData(
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

    private fun readPlayerGameData(playerId: PlayerId): List<PlayerGameData> =
        lblcs.playersQueries.readGameDataByPlayerId(playerId.id).executeAsList().let { data ->
            data.map {
                it.toPlayerGameData()
            }
        }

    private fun Players.toPlayer(): Player {
        val gameData by lazy {
            readPlayerGameData(PlayerId(this.id))
        }
        return Player(
            PlayerId(this.id),
            this.summoner_name,
            this.riot_puuid,
            this.team_id?.let { TeamId(it) },
            gameData,
        )
    }

    private fun Player_game_data.toPlayerGameData() = PlayerGameData(
        this.kills,
        this.deaths,
        this.assists,
        this.level,
        this.gold,
        this.vision_score,
        this.damage,
        this.healing,
        this.shielding,
        this.damage_taken,
        this.self_mitigated_damage,
        this.damage_to_turrets,
        this.longest_life,
        this.double_kills,
        this.triple_kills,
        this.quadra_kills,
        this.penta_kills,
        this.cs,
        this.champion_name,
        this.item0,
        this.item1,
        this.item2,
        this.item3,
        this.item4,
        this.item5,
        this.trinket,
        this.keystone_rune,
        this.secondary_tree,
        this.summoner1,
        this.summoner2
    )
}