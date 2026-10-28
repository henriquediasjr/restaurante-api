package br.pucpr.authserver.products

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
@Validated
class ProductController(
    val service: ProductService
) {
    @GetMapping
    fun list() = service.findAll()
        .map { ProductResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = service.findByIdOrNull(id)
        ?.let { ProductResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @PostMapping
    fun insert(
        @Valid @RequestBody product: ProductRequest
    ) = service.insert(product)
        .let { ProductResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}")
    fun update(
        @Min(1) @PathVariable id: Long,
        @Valid @RequestBody product: ProductRequest
    ) = service.update(id, product)
        .let { ProductResponse(it) }
        .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    fun deleteById(
        @Min(1) @PathVariable id: Long
    ): ResponseEntity<Void> = service.deleteById(id)
        .let { ResponseEntity.noContent().build() }
}
