package br.pucpr.authserver.orders

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderRequest(
    @NotNull
    @Min(1)
    val userId: Long?
)
