package br.pucpr.authserver.tags

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.util.Objects

@Entity
class Tag(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var name: String
) {
    final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (this::class !== other::class) return false
        return (other as Tag).id == id
    }

    final override fun hashCode(): Int = Objects.hash(id)
}
