package br.pucpr.authserver.products

import br.pucpr.authserver.categories.CategoryRepository
import br.pucpr.authserver.tags.TagRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ProductService(
    val repository: ProductRepository,
    val categoryRepository: CategoryRepository,
    val tagRepository: TagRepository
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

    @Transactional
    fun addTag(id: Long, tagName: String): Boolean {
        val product = findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product $id not found!")
        val name = tagName.lowercase()
        if (product.tags.any { it.name == name }) return false
        val tag = tagRepository.findByName(name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Tag $name not found!")
        product.tags.add(tag)
        repository.save(product)
        log.info("Tag {} added to product {}", name, id)
        return true
    }

    @Transactional
    fun removeTag(id: Long, tagName: String): Boolean {
        val product = findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product $id not found!")
        val removed = product.tags.removeIf { it.name == tagName.lowercase() }
        if (removed) {
            repository.save(product)
            log.info("Tag {} removed from product {}", tagName, id)
        }
        return removed
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductService::class.java)
    }
}
