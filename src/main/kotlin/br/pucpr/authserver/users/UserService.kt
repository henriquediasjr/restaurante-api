package br.pucpr.authserver.users

import br.pucpr.authserver.roles.RoleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository
) {
    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }
        return repository
            .save(user)
            .also { log.info("User {} created!", it.id) }
    }

    fun update(id: Long, name: String): User? {
        val user = repository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User $id not found!")
        if (user.name == name) return null
        user.name = name
        return repository.save(user)
    }

    fun findAll(dir: SortDir) =
        if (dir == SortDir.ASC)
            repository.findAll(Sort.by("name"))
        else
            repository.findAll(Sort.by("name").descending())

    fun findByRole(role: String) = repository.findByRole(role.uppercase())
    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)
    fun deleteById(id: Long) = repository
        .deleteById(id)
        .also { log.warn("User {} deleted", id) }
    fun addRole(id: Long, roleName: String): Boolean {
        val upperRole = roleName.uppercase()
        val user = findByIdOrNull(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User $id not found!")
        if (user.roles.any { it.name == upperRole }) return false
        val role = roleRepository.findByName(upperRole) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Role $upperRole not found!"
        )

        user.roles.add(role)
        repository.save(user)
        return true
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}