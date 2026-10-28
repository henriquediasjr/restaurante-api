package br.pucpr.authserver.tags

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
@Validated
class TagController(
    val service: TagService
) {
    @GetMapping
    fun list() = service.findAll()
        .map { TagResponse(it) }
        .let { ResponseEntity.ok(it) }

    @PostMapping
    fun insert(
        @Valid @RequestBody tag: TagRequest
    ) = service.insert(tag.toTag())
        .let { TagResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
}
