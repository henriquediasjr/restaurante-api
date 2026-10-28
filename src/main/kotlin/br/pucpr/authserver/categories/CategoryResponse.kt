package br.pucpr.authserver.categories

data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String
) {
    constructor(category: Category) : this(
        id = category.id!!,
        name = category.name,
        description = category.description
    )
}
