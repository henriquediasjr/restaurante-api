package br.pucpr.authserver

import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class BootStrapper(
    val roleRepository: RoleRepository,
    val userRepository: UserRepository
) : ApplicationListener<ContextRefreshedEvent> {

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN") ?: roleRepository
            .save(Role(name = "ADMIN", description = "System Administrator"))
        roleRepository.findByName("PREMIUM") ?: roleRepository.save(
            Role(
                name = "PREMIUM",
                description = "Premium User"
            )
        )
        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                name = "Administrator",
                password = "admin",
                email = "admin@authserver.com"
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }
    }
}