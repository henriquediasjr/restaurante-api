package br.pucpr.authserver.products

import br.pucpr.authserver.categories.Category
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity
class Product(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCategory")
    var category: Category
)
