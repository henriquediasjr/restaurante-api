package br.pucpr.authserver.users

import br.pucpr.authserver.roles.Role

fun oneUser(
    id: Long? = 1L,
    name: String = "John Doe",
    email: String = "${name.lowercase().replace(" ", ".")}@email.com",
    password: String = "password",
    roles: MutableSet<Role> = mutableSetOf()
) = User(id = id, email = email, password = password, name = name, roles = roles)
