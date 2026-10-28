package br.pucpr.authserver.orders

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
@Validated
class OrderController(
    val service: OrderService
) {
    @GetMapping
    fun list(@RequestParam userId: Long?) = service.findAll(userId)
        .map { OrderResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = service.findByIdOrNull(id)
        ?.let { OrderResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @PostMapping
    fun insert(
        @Valid @RequestBody order: OrderRequest
    ) = service.insert(order.userId!!)
        .let { OrderResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}/products/{productId}")
    fun addProduct(
        @Min(1) @PathVariable id: Long,
        @Min(1) @PathVariable productId: Long
    ): ResponseEntity<Void> =
        if (service.addProduct(id, productId)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()

    @DeleteMapping("/{id}/products/{productId}")
    fun removeProduct(
        @Min(1) @PathVariable id: Long,
        @Min(1) @PathVariable productId: Long
    ): ResponseEntity<Void> =
        if (service.removeProduct(id, productId)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()

    @DeleteMapping("/{id}")
    fun deleteById(
        @Min(1) @PathVariable id: Long
    ): ResponseEntity<Void> = service.deleteById(id)
        .let { ResponseEntity.noContent().build() }
}
