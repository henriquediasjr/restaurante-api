package br.pucpr.authserver.users

import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.JWT
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder,
    val jwt: JWT
) {
    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }
        user.password = passwordEncoder.encode(user.password)!!
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

    fun login(email: String, password: String): LoginResponse {
        val user = repository.findByEmail(email)
        if (user == null || !passwordEncoder.matches(password, user.password)) {
            log.warn("Failed login attempt for {}", email)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        }
        log.info("User {} logged in", user.id)
        return LoginResponse(jwt.createToken(user), UserResponse(user))
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}