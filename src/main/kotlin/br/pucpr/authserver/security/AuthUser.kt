package br.pucpr.authserver.security

data class AuthUser(
    val id: Long,
    val name: String,
    val roles: List<String>
)
