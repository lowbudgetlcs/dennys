package com.lowbudgetlcs.repositories.user

import com.lowbudgetlcs.domain.models.users.NewUser
import com.lowbudgetlcs.domain.models.users.Password
import com.lowbudgetlcs.domain.models.users.User
import com.lowbudgetlcs.domain.models.users.UserId
import com.lowbudgetlcs.domain.models.users.Username
import com.lowbudgetlcs.domain.models.users.toPassword
import com.lowbudgetlcs.domain.models.users.toUserId
import com.lowbudgetlcs.domain.models.users.toUsername
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.USERS

class UserRepository(
    private val dsl: DSLContext,
) : IUserRepository {
    override fun getById(id: UserId): User? = selectUsers().where(USERS.ID.eq(id.value)).fetchOne()?.let(::rowToUser)

    override fun getByName(username: Username): User? =
        selectUsers().where(USERS.USERNAME.eq(username.value)).fetchOne()?.let(::rowToUser)

    override fun insert(
        newUser: NewUser,
        passwordHash: String,
        salt: String,
    ): User? {
        val insertedId =
            dsl
                .insertInto(USERS)
                .set(USERS.USERNAME, newUser.username.value)
                .set(USERS.PASSWORD, passwordHash)
                .set(USERS.SALT, salt)
                .returning(USERS.ID)
                .fetchOne()
                ?.get(USERS.ID)

        return insertedId?.toUserId()?.let(::getById)
    }

    override fun getPasswordById(id: UserId): Password? =
        selectUsers().where(USERS.ID.eq(id.value)).fetchOne()?.let {
            it[USERS.PASSWORD]?.toPassword()
        }

    private fun selectUsers() = dsl.select(USERS.ID, USERS.USERNAME, USERS.PASSWORD, USERS.SALT).from(USERS)

    private fun rowToUser(row: Record): User? {
        val id = row[USERS.ID]?.toUserId() ?: return null
        val username = row[USERS.USERNAME]?.toUsername() ?: return null
        val salt = row[USERS.SALT] ?: return null

        return User(id = id, username = username, salt = salt)
    }
}
