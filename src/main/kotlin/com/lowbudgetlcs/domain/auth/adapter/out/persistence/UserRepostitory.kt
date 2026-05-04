package com.lowbudgetlcs.domain.auth.adapter.out.persistence

import com.lowbudgetlcs.domain.auth.core.models.NewUser
import com.lowbudgetlcs.domain.auth.core.models.User
import com.lowbudgetlcs.domain.auth.core.models.types.UserId
import com.lowbudgetlcs.domain.auth.core.models.types.Username
import com.lowbudgetlcs.domain.auth.core.models.types.toUserId
import com.lowbudgetlcs.domain.auth.core.models.types.toUsername
import com.lowbudgetlcs.domain.auth.core.port.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.USERS

class UserRepostitory(
    private val dsl: DSLContext,
) : IUserRepository {
    override suspend fun getAll(): List<User> = withContext(Dispatchers.IO) {
        selectUsers().fetch(::rowToUser)
    }

    override suspend fun getById(id: UserId): User? =
        withContext(Dispatchers.IO) {
            selectUsers().where(USERS.ID.eq(id.value)).fetchOne(::rowToUser)
        }

    override suspend fun getByUsername(username: Username): User? =
        withContext(Dispatchers.IO) {
            selectUsers().where(USERS.USERNAME.eq(username.value)).fetchOne(::rowToUser)
        }

    override suspend fun insert(newUser: NewUser): User? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl
                    .insertInto(
                        USERS,
                    ).set(USERS.USERNAME, newUser.username.value)
                    .set(USERS.PASSWORD_HASH, newUser.passwordHash)
                    .set(USERS.IS_ACTIVE, newUser.isActive)
                    .set(USERS.ROLES, newUser.roles.joinToString(":"))
                    .returning(USERS.ID)
                    .fetchOne()
            }
                ?.get(USERS.ID)
        return insertedId?.toUserId()?.let { getById(it) }
    }

    private fun selectUsers() =
        dsl
            .select(
                USERS.ID,
                USERS.USERNAME,
                USERS.PASSWORD_HASH,
                USERS.IS_ACTIVE,
                USERS.ROLES,
            ).from(USERS)

    fun rowToUser(row: Record): User? {
        val userId = row[USERS.ID]?.toUserId() ?: return null
        val username = row[USERS.USERNAME] ?: return null
        val password = row[USERS.PASSWORD_HASH] ?: return null
        val isActive = row[USERS.IS_ACTIVE] ?: return null
        val roles = row[USERS.ROLES]?.split(":")?.toSet() ?: return null
        return User(
            id = userId,
            username = username.toUsername(),
            passwordHash = password,
            isActive = isActive,
            roles = roles,
        )
    }
}
