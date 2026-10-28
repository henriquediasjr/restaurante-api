package br.pucpr.authserver.users.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @Email
    var email: String?,

    @NotBlank
    var password: String?
)