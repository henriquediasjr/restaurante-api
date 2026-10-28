package br.pucpr.authserver.users.requests

import jakarta.validation.constraints.NotBlank

data class PatchUserRequest(
    @NotBlank
    var name: String?
)
