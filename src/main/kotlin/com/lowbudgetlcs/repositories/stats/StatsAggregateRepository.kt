package com.lowbudgetlcs.repositories.stats

import com.lowbudgetlcs.domain.InvalidRequestException
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.stats.models.ChampionAggregate
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.StatTotals
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.stats.models.TeamAggregate
import com.lowbudgetlcs.domain.team.models.toTeamId
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SortField
import org.jooq.impl.DSL.coalesce
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.countDistinct
import org.jooq.impl.DSL.max
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.nullif
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.sum
import org.jooq.impl.DSL.unnest
import org.jooq.impl.SQLDataType
import org.jooq.storage.tables.references.PLAYERS
import org.jooq.storage.tables.references.PLAYER_GAME_STATS_V1
import org.jooq.storage.tables.references.SERIES
import org.jooq.storage.tables.references.SERIES_RESULTS
import org.jooq.storage.tables.references.TEAMS
import org.jooq.storage.tables.references.TEAM_GAME_STATS_V1
import java.math.BigDecimal

private val P = PLAYER_GAME_STATS_V1
private val T = TEAM_GAME_STATS_V1

private val PLAYER_STATS =
    listOf(
        "kills" to P.KILLS,
        "deaths" to P.DEATHS,
        "assists" to P.ASSISTS,
        "cs" to P.CS,
        "goldEarned" to P.GOLD_EARNED,
        "damageToChampions" to P.DAMAGE_TO_CHAMPIONS,
        "damageTaken" to P.DAMAGE_TAKEN,
        "visionScore" to P.VISION_SCORE,
        "wardsPlaced" to P.WARDS_PLACED,
        "wardsKilled" to P.WARDS_KILLED,
        "controlWardsBought" to P.CONTROL_WARDS_BOUGHT,
        "voidGrubs" to P.VOID_GRUBS,
        "gameDurationSeconds" to P.GAME_DURATION_S,
    )

class StatsAggregateRepository(
    private val dsl: DSLContext,
) : IStatsAggregateRepository {
    override fun getPlayerAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
    ): List<PlayerAggregate> {
        val complete = SERIES.STATUS.eq(SeriesStatus.COMPLETE.name)
        val games = count()
        val wins = count().filterWhere(P.WIN.isTrue)
        val seriesPlayed = countDistinct(P.SERIES_ID)
        val seriesWon =
            countDistinct(
                P.SERIES_ID,
            ).filterWhere(complete.and(SERIES_RESULTS.WINNER_TEAM_ID.eq(P.TEAM_ID)))
        val seriesLost =
            countDistinct(P.SERIES_ID).filterWhere(
                complete
                    .and(SERIES_RESULTS.SERIES_ID.isNotNull)
                    .and(SERIES_RESULTS.LOSER_TEAM_ID.eq(P.TEAM_ID).or(SERIES_RESULTS.WINNER_TEAM_ID.isNull)),
            )
        val playerName = max(PLAYERS.NAME)
        val totals = PLAYER_STATS.associate { (stat, column) -> stat to sum(column) }
        val counts =
            mapOf(
                "games" to games,
                "wins" to wins,
                "seriesPlayed" to seriesPlayed,
                "seriesWon" to seriesWon,
                "seriesLost" to seriesLost,
            )
        val order = sortBy(counts + totals + perGame(totals, games), sort)
        return dsl
            .select(P.PLAYER_ID, playerName, games, wins, seriesPlayed, seriesWon, seriesLost)
            .select(totals.values)
            .from(P)
            .join(SERIES)
            .on(SERIES.ID.eq(P.SERIES_ID))
            .leftJoin(SERIES_RESULTS)
            .on(SERIES_RESULTS.SERIES_ID.eq(P.SERIES_ID))
            .leftJoin(PLAYERS)
            .on(PLAYERS.ID.eq(P.PLAYER_ID))
            .where(playerConditions(query) + P.PLAYER_ID.isNotNull)
            .groupBy(P.PLAYER_ID)
            .orderBy(order, P.PLAYER_ID.asc())
            .limit(limit)
            .offset(offset)
            .fetch { r ->
                PlayerAggregate(
                    playerId = r.get(P.PLAYER_ID)!!.toPlayerId(),
                    playerName = r.get(playerName),
                    games = r.get(games),
                    wins = r.get(wins),
                    seriesPlayed = r.get(seriesPlayed),
                    seriesWon = r.get(seriesWon),
                    seriesLost = r.get(seriesLost),
                    totals = r.statTotals(totals),
                )
            }
    }

    override fun getTeamAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
    ): List<TeamAggregate> {
        val games = count()
        val wins = count().filterWhere(T.WIN.isTrue)
        val teamName = max(TEAMS.NAME)
        val totals: Map<String, Field<out Number>> =
            mapOf(
                "firstBloods" to count().filterWhere(T.FIRST_BLOOD.isTrue),
                "firstTowers" to count().filterWhere(T.FIRST_TOWER.isTrue),
                "firstDragons" to count().filterWhere(T.FIRST_DRAGON.isTrue),
                "firstBarons" to count().filterWhere(T.FIRST_BARON.isTrue),
                "kills" to sum(T.KILLS),
                "towerKills" to sum(T.TOWER_KILLS),
                "inhibitorKills" to sum(T.INHIBITOR_KILLS),
                "dragonKills" to sum(T.DRAGON_KILLS),
                "baronKills" to sum(T.BARON_KILLS),
                "riftHeraldKills" to sum(T.RIFT_HERALD_KILLS),
                "hordeKills" to sum(T.HORDE_KILLS),
                "gameDurationSeconds" to sum(T.GAME_DURATION_S),
            )
        val order = sortBy(mapOf("games" to games, "wins" to wins) + totals + perGame(totals, games), sort)
        return dsl
            .select(T.TEAM_ID, teamName, games, wins)
            .select(totals.values)
            .from(T)
            .leftJoin(TEAMS)
            .on(TEAMS.ID.eq(T.TEAM_ID))
            .where(teamConditions(query) + T.TEAM_ID.isNotNull)
            .groupBy(T.TEAM_ID)
            .orderBy(order, T.TEAM_ID.asc())
            .limit(limit)
            .offset(offset)
            .fetch { r ->
                fun total(stat: String) = r.long(totals.getValue(stat))
                TeamAggregate(
                    teamId = r.get(T.TEAM_ID)!!.toTeamId(),
                    teamName = r.get(teamName),
                    games = r.get(games),
                    wins = r.get(wins),
                    firstBloods = total("firstBloods"),
                    firstTowers = total("firstTowers"),
                    firstDragons = total("firstDragons"),
                    firstBarons = total("firstBarons"),
                    kills = total("kills"),
                    towerKills = total("towerKills"),
                    inhibitorKills = total("inhibitorKills"),
                    dragonKills = total("dragonKills"),
                    baronKills = total("baronKills"),
                    riftHeraldKills = total("riftHeraldKills"),
                    hordeKills = total("hordeKills"),
                    gameDurationSeconds = total("gameDurationSeconds"),
                )
            }
    }

    override fun getChampionAggregates(
        query: StatsQuery,
        sort: StatsSort,
        limit: Int,
        offset: Int,
    ): List<ChampionAggregate> {
        val picked =
            name("picked").`as`(
                select(
                    P.CHAMPION_ID.`as`("champion_id"),
                    max(P.CHAMPION_NAME).`as`("champion_name"),
                    count().`as`("picks"),
                    count().filterWhere(P.WIN.isTrue).`as`("wins"),
                ).select(PLAYER_STATS.map { (stat, column) -> sum(column).`as`(stat) })
                    .from(P)
                    .where(playerConditions(query))
                    .groupBy(P.CHAMPION_ID),
            )
        val bannedTable = unnest(T.BANS).`as`("u", "champion_id")
        val bannedId = bannedTable.field("champion_id", SQLDataType.INTEGER)!!
        val banned =
            name("banned").`as`(
                select(bannedId.`as`("champion_id"), count().`as`("bans"))
                    .from(T)
                    .crossJoin(bannedTable)
                    .where(teamConditions(query) + listOfNotNull(query.championId?.let { bannedId.eq(it) }))
                    .groupBy(bannedId),
            )
        val pickedId = picked.field("champion_id", SQLDataType.INTEGER)!!
        val championId = coalesce(pickedId, banned.field("champion_id", SQLDataType.INTEGER))
        val championName = picked.field("champion_name", SQLDataType.VARCHAR)!!
        val picks = coalesce(picked.field("picks", SQLDataType.INTEGER), 0)
        val wins = coalesce(picked.field("wins", SQLDataType.INTEGER), 0)
        val bans = coalesce(banned.field("bans", SQLDataType.INTEGER), 0)
        val totals =
            PLAYER_STATS.associate { (stat, _) ->
                stat to
                    coalesce(picked.field(stat, SQLDataType.NUMERIC), BigDecimal.ZERO)
            }
        val order =
            sortBy(mapOf("picks" to picks, "wins" to wins, "bans" to bans) + totals + perGame(totals, picks), sort)
        return dsl
            .with(picked, banned)
            .select(championId, championName, picks, wins, bans)
            .select(totals.values)
            .from(picked)
            .fullJoin(banned)
            .on(pickedId.eq(banned.field("champion_id", SQLDataType.INTEGER)))
            .orderBy(order, championId.asc())
            .limit(limit)
            .offset(offset)
            .fetch { r ->
                ChampionAggregate(
                    championId = r.get(championId),
                    championName = r.get(championName),
                    picks = r.get(picks),
                    wins = r.get(wins),
                    bans = r.get(bans),
                    totals = r.statTotals(totals),
                )
            }
    }

    private fun perGame(
        totals: Map<String, Field<out Number>>,
        games: Field<out Number>,
    ): Map<String, Field<BigDecimal>> =
        totals.entries.associate { (stat, total) ->
            "${stat}PerGame" to
                coalesce(
                    total.cast(SQLDataType.NUMERIC).div(nullif(games.cast(SQLDataType.NUMERIC), BigDecimal.ZERO)),
                    BigDecimal.ZERO,
                )
        }

    private fun sortBy(
        sortable: Map<String, Field<*>>,
        sort: StatsSort,
    ): SortField<*> {
        val field =
            sortable[sort.stat]
                ?: throw InvalidRequestException(
                    "Unknown sort '${sort.stat}'. Sortable stats: ${sortable.keys.joinToString()}.",
                )
        return if (sort.descending) field.desc().nullsLast() else field.asc().nullsLast()
    }

    private fun Record.long(field: Field<out Number>): Long = get(field)?.toLong() ?: 0L

    private fun Record.statTotals(totals: Map<String, Field<out Number>>): StatTotals {
        fun total(stat: String) = long(totals.getValue(stat))
        return StatTotals(
            kills = total("kills"),
            deaths = total("deaths"),
            assists = total("assists"),
            cs = total("cs"),
            goldEarned = total("goldEarned"),
            damageToChampions = total("damageToChampions"),
            damageTaken = total("damageTaken"),
            visionScore = total("visionScore"),
            wardsPlaced = total("wardsPlaced"),
            wardsKilled = total("wardsKilled"),
            controlWardsBought = total("controlWardsBought"),
            voidGrubs = total("voidGrubs"),
            gameDurationSeconds = total("gameDurationSeconds"),
        )
    }
}
