package com.lowbudgetlcs.repositories.user

import com.lowbudgetlcs.domain.models.auth.NewUser
import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.toUserId
import com.lowbudgetlcs.domain.models.auth.toUsername
import com.lowbudgetlcs.domain.models.auth.types.UserId
import com.lowbudgetlcs.domain.models.auth.types.Username
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.USERS

class UserRepostitory(
    private val dsl: DSLContext,
) : IUserRepository {
    override fun getAll(): List<User> = selectUsers().fetch(::rowToUser)

    override fun getById(id: UserId): User? = selectUsers().where(USERS.ID.eq(id.value)).fetchOne(::rowToUser)

    override fun getByUsername(username: Username): User? =
        selectUsers().where(USERS.USERNAME.eq(username.value)).fetchOne(::rowToUser)

    override fun insert(newUser: NewUser): User? {
        val insertedId =
            dsl
                .insertInto(
                    USERS,
                ).set(USERS.USERNAME, newUser.username.value)
                .set(USERS.PASSWORD_HASH, newUser.passwordHash)
                .set(USERS.IS_ACTIVE, newUser.isActive)
                .set(USERS.ROLES, newUser.roles.joinToString(":"))
                .returning(USERS.ID)
                .fetchOne()
                ?.get(USERS.ID)
        return insertedId?.toUserId()?.let(::getById)
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
