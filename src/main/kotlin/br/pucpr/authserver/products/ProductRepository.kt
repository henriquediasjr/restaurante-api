package br.pucpr.authserver.products

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun existsByCategoryId(categoryId: Long): Boolean

    @Query(
        """
            select p from Product p
            where (:name is null or lower(p.name) like lower(concat('%', cast(:name as string), '%')))
              and (:categoryId is null or p.category.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:tag is null or exists (select t from p.tags t where t.name = :tag))
        """
    )
    fun search(
        name: String?,
        categoryId: Long?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        tag: String?,
        sort: Sort
    ): List<Product>
}
