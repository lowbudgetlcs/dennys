package com.lowbudgetlcs.repositories.player

import com.lowbudgetlcs.domain.models.player.NewPlayer
import com.lowbudgetlcs.domain.models.player.Player
import com.lowbudgetlcs.domain.models.player.PlayerId
import com.lowbudgetlcs.domain.models.player.PlayerName
import com.lowbudgetlcs.domain.models.player.toPlayerId
import com.lowbudgetlcs.domain.models.team.TeamId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.PLAYERS
import org.jooq.storage.tables.references.PLAYERS_TO_TEAM

class PlayerRepository(
    private val dsl: DSLContext,
) : IPlayerRepository {
    override fun getAll(): List<Player> = selectPlayers().fetch().mapNotNull(::rowToPlayer)

    override fun getById(id: PlayerId): Player? = selectPlayers().where(PLAYERS.ID.eq(id.value)).fetchOne(::rowToPlayer)

    override fun getByTeamId(teamId: TeamId): List<Player> =
        dsl
            .select(PLAYERS.ID, PLAYERS.NAME)
            .from(PLAYERS.innerJoin(PLAYERS_TO_TEAM).on(PLAYERS.ID.eq(PLAYERS_TO_TEAM.PLAYER_ID)))
            .where(PLAYERS_TO_TEAM.TEAM_ID.eq(teamId.value))
            .fetch()
            .mapNotNull(::rowToPlayer)

    override fun insert(newPlayer: NewPlayer): Player? {
        val insertedId =
            dsl
                .insertInto(PLAYERS)
                .set(PLAYERS.NAME, newPlayer.name.value)
                .returning(PLAYERS.ID)
                .fetchOne()
                ?.get(PLAYERS.ID)

        return insertedId?.toPlayerId()?.let(::getById)
    }

    override fun renamePlayer(
        id: PlayerId,
        newName: PlayerName,
    ): Player? {
        val updated =
            dsl
                .update(PLAYERS)
                .set(PLAYERS.NAME, newName.value)
                .where(PLAYERS.ID.eq(id.value))
                .execute()

        return if (updated > 0) getById(id) else null
    }

    private fun selectPlayers() = dsl.select(PLAYERS.ID, PLAYERS.NAME).from(PLAYERS)

    private fun rowToPlayer(row: Record): Player? {
        val playerId = row[PLAYERS.ID]?.toPlayerId() ?: return null
        val name = row[PLAYERS.NAME]?.let { PlayerName(it) } ?: return null

        return Player(
            id = playerId,
            name = name,
        )
    }
}
