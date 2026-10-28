package br.pucpr.authserver.users.requests

import br.pucpr.authserver.users.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserRequest(
    @NotNull
    @Email
    var email: String?,

    @NotBlank
    @Size(min=4)
    var password: String?,

    @NotBlank
    var name: String?
) {
    fun toUser() = User(
        id = null,
        email = email!!,
        password = password!!,
        name = name!!,
        roles = mutableSetOf()
    )
}
