package br.pucpr.authserver.categories

import jakarta.validation.constraints.NotBlank

data class CategoryRequest(
    @NotBlank
    val name: String?,

    @NotBlank
    val description: String?
) {
    fun toCategory() = Category(
        id = null,
        name = name!!,
        description = description!!
    )
}
