package br.pucpr.authserver.products

import br.pucpr.authserver.users.SortDir
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/products")
@Validated
class ProductController(
    val service: ProductService
) {
    @GetMapping
    fun list(
        @RequestParam name: String?,
        @RequestParam categoryId: Long?,
        @RequestParam minPrice: BigDecimal?,
        @RequestParam maxPrice: BigDecimal?,
        @RequestParam tag: String?,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "ASC") sortDir: String
    ) = service
        .search(name, categoryId, minPrice, maxPrice, tag, sortBy, SortDir.findOrThrow(sortDir))
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

    @PutMapping("/{id}/tags/{name}")
    fun addTag(
        @Min(1) @PathVariable id: Long,
        @PathVariable name: String
    ): ResponseEntity<Void> =
        if (service.addTag(id, name)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()

    @DeleteMapping("/{id}/tags/{name}")
    fun removeTag(
        @Min(1) @PathVariable id: Long,
        @PathVariable name: String
    ): ResponseEntity<Void> =
        if (service.removeTag(id, name)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()
}
