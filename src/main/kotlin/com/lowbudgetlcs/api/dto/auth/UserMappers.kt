package com.lowbudgetlcs.api.dto.auth

import com.lowbudgetlcs.domain.models.users.NewUser
import com.lowbudgetlcs.domain.models.users.User
import com.lowbudgetlcs.domain.models.users.toPassword
import com.lowbudgetlcs.domain.models.users.toUsername

fun CreateUserDto.toNewUser(): NewUser =
    NewUser(
        username = username.toUsername(),
        password = password.toPassword(),
    )

fun User.toDto(): UserDto =
    UserDto(
        id = id.value,
        username = username.value,
    )
