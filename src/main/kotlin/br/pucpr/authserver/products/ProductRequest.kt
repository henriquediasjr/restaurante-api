package br.pucpr.authserver.products

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ProductRequest(
    @NotBlank
    val name: String?,

    @NotBlank
    val description: String?,

    @NotNull
    @DecimalMin("0.01")
    val price: BigDecimal?,

    @NotNull
    @Min(1)
    val categoryId: Long?
)
