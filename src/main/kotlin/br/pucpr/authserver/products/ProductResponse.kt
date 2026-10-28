package br.pucpr.authserver.products

import br.pucpr.authserver.categories.CategoryResponse
import br.pucpr.authserver.tags.TagResponse
import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val category: CategoryResponse,
    val tags: List<TagResponse>
) {
    constructor(product: Product) : this(
        id = product.id!!,
        name = product.name,
        description = product.description,
        price = product.price,
        category = CategoryResponse(product.category),
        tags = product.tags.map { TagResponse(it) }.sortedBy { it.name }
    )
}
