package br.pucpr.authserver.roles

import br.pucpr.authserver.users.SortDir
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RoleService(
    val repository: RoleRepository
) {
    fun insert(role: Role): Role {
        role.name = role.name.uppercase()
        if (repository.findByName(role.name) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Role ${role.name} already exists")
        }
        return repository.save(role)
    }

    fun findAll() = repository.findAll(Sort.by("name"))
}