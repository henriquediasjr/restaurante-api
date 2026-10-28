package br.pucpr.authserver.users.responses

import br.pucpr.authserver.users.User

data class UserResponse(
    var id: Long,
    var email: String,
    var name: String
) {
    constructor(user: User) :
            this(
                id = user.id!!,
                email = user.email,
                name = user.name
            )
}
