package br.pucpr.authserver.roles

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.util.Objects

@Entity
class Role (
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,
) {
    final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (this::class !== other::class) return false
        val o = other as Role
        return o.id == id
    }

    final override fun hashCode(): Int = Objects.hash(id)
}