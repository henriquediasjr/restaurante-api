package br.pucpr.authserver.products

import br.pucpr.authserver.categories.CategoryResponse
import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val category: CategoryResponse
) {
    constructor(product: Product) : this(
        id = product.id!!,
        name = product.name,
        description = product.description,
        price = product.price,
        category = CategoryResponse(product.category)
    )
}
