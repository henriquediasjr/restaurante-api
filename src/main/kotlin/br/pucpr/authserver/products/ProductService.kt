package br.pucpr.authserver.products

import br.pucpr.authserver.categories.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProductService(
    val repository: ProductRepository,
    val categoryRepository: CategoryRepository
) {
    fun insert(request: ProductRequest): Product {
        val category = categoryRepository.findByIdOrNull(request.categoryId!!)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Category ${request.categoryId} not found!")
        val product = Product(
            name = request.name!!,
            description = request.description!!,
            price = request.price!!,
            category = category
        )
        return repository
            .save(product)
            .also { log.info("Product {} created!", it.id) }
    }

    fun findAll() = repository.findAll(Sort.by("name"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun update(id: Long, request: ProductRequest): Product {
        val product = findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product $id not found!")
        val category = categoryRepository.findByIdOrNull(request.categoryId!!)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Category ${request.categoryId} not found!")
        product.name = request.name!!
        product.description = request.description!!
        product.price = request.price!!
        product.category = category
        return repository
            .save(product)
            .also { log.info("Product {} updated", id) }
    }

    fun deleteById(id: Long) {
        val product = findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product $id not found!")
        repository.delete(product)
        log.warn("Product {} deleted", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductService::class.java)
    }
}
