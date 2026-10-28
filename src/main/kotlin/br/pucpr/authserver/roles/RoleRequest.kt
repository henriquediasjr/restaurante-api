package br.pucpr.authserver.roles

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

import org.jetbrains.annotations.NotNull

data class RoleRequest(
    @NotNull
    @Pattern(regexp="[A-Z][A-Z0-9]+")
    val name: String?,

    @NotBlank
    val description: String?
) {
    fun toRole() = Role(
        id = null,
        name = name!!,
        description = description!!
    )
}
