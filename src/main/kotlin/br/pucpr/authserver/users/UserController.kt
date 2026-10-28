package br.pucpr.authserver.users

import br.pucpr.authserver.users.requests.PatchUserRequest
import br.pucpr.authserver.users.requests.UserRequest
import br.pucpr.authserver.users.responses.UserResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Validated
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun list(
        @RequestParam sortDir: String?,
        @RequestParam role: String?
    ): ResponseEntity<List<UserResponse>> {
        val users = if (role != null) userService.findByRole(role)
        else SortDir.findOrThrow(sortDir ?: "ASC")
            .let { userService.findAll(it) }

        return users
            .map { UserResponse(it) }
            .let { ResponseEntity.ok(it) }
    }


    @PostMapping
    fun insert(
        @Valid @RequestBody user: UserRequest
    ) = userService.insert(user.toUser())
        .let { UserResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PatchMapping("/{id}")
    fun update(
        @Min(1) @PathVariable id: Long,
        @Valid @RequestBody user: PatchUserRequest
    ) = userService.update(id, user.name!!)
        ?.let { UserResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.noContent().build()

    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = userService.findByIdOrNull(id)
        ?.let { UserResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    fun deleteById(
        @Min(1) @PathVariable id: Long
    ): ResponseEntity<Void> = userService.deleteById(id)
        .let { ResponseEntity.ok().build() }

    @PutMapping("/{id}/roles/{name}")
    fun grant(
        @Min(1) @PathVariable id: Long,
        @PathVariable name: String
    ): ResponseEntity<Void> =
        if (userService.addRole(id, name)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()
}