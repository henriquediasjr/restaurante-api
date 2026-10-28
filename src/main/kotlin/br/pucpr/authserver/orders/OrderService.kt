package br.pucpr.authserver.orders

import br.pucpr.authserver.products.ProductRepository
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class OrderService(
    val repository: OrderRepository,
    val userRepository: UserRepository,
    val productRepository: ProductRepository
) {
    fun insert(userId: Long): Order {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User $userId not found!")
        return repository
            .save(Order(user = user))
            .also { log.info("Order {} created for user {}", it.id, userId) }
    }

    fun findAll(userId: Long?) =
        if (userId != null) repository.findByUserIdOrderByCreatedAtDesc(userId)
        else repository.findAll()

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    @Transactional
    fun addProduct(id: Long, productId: Long): Boolean {
        val order = findOrThrow(id)
        if (order.products.any { it.id == productId }) return false
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product $productId not found!")
        order.products.add(product)
        repository.save(order)
        log.info("Product {} added to order {}", productId, id)
        return true
    }

    @Transactional
    fun removeProduct(id: Long, productId: Long): Boolean {
        val order = findOrThrow(id)
        val removed = order.products.removeIf { it.id == productId }
        if (removed) {
            repository.save(order)
            log.info("Product {} removed from order {}", productId, id)
        }
        return removed
    }

    fun deleteById(id: Long) {
        repository.delete(findOrThrow(id))
        log.warn("Order {} deleted", id)
    }

    private fun findOrThrow(id: Long) = findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Order $id not found!")

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)
    }
}
