package br.pucpr.authserver.categories

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CategoryService(
    val repository: CategoryRepository
) {
    fun insert(category: Category): Category {
        if (repository.findByName(category.name) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Category ${category.name} already exists")
        }
        return repository
            .save(category)
            .also { log.info("Category {} created!", it.id) }
    }

    fun findAll() = repository.findAll(Sort.by("name"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun update(id: Long, name: String, description: String): Category {
        val category = findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Category $id not found!")
        val sameName = repository.findByName(name)
        if (sameName != null && sameName.id != id) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Category $name already exists")
        }
        category.name = name
        category.description = description
        return repository
            .save(category)
            .also { log.info("Category {} updated", id) }
    }

    fun deleteById(id: Long) {
        val category = findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Category $id not found!")
        repository.delete(category)
        log.warn("Category {} deleted", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CategoryService::class.java)
    }
}
