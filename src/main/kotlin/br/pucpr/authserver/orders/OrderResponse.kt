package br.pucpr.authserver.orders

import br.pucpr.authserver.products.ProductResponse
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val products: List<ProductResponse>,
    val total: BigDecimal
) {
    constructor(order: Order) : this(
        id = order.id!!,
        userId = order.user.id!!,
        createdAt = order.createdAt,
        products = order.products.map { ProductResponse(it) }.sortedBy { it.name },
        total = order.products.fold(BigDecimal.ZERO) { sum, p -> sum + p.price }
    )
}
