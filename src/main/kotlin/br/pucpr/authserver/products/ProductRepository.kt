package br.pucpr.authserver.products

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun existsByCategoryId(categoryId: Long): Boolean
}
