package br.pucpr.authserver.users.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @NotBlank
    @Email
    var email: String?,

    @NotBlank
    var password: String?
)