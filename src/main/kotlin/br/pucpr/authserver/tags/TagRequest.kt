package br.pucpr.authserver.tags

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class TagRequest(
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9-]+")
    val name: String?
) {
    fun toTag() = Tag(id = null, name = name!!)
}
