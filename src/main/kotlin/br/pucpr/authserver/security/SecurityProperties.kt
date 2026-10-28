package br.pucpr.authserver.security

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("security")
data class SecurityProperties(
    @field:NotBlank val issuer: String,
    @field:Size(min = 32) val secret: String,
    @field:Min(1) val expireHours: Long,
    @field:Min(1) val adminExpireHours: Long
)
