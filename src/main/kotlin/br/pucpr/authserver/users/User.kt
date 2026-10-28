package br.pucpr.authserver.users

import br.pucpr.authserver.roles.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Email

@Entity
@Table(name="UserTable")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Email
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, length = 72)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @ManyToMany
    @JoinTable(
        name = "UserRole",
        joinColumns = [JoinColumn(name="idUser")],
        inverseJoinColumns = [JoinColumn(name="idRole")],
    )
    var roles: MutableSet<Role> = mutableSetOf()
)