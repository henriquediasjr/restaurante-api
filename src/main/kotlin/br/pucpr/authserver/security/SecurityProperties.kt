package br.pucpr.authserver.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security")
data class SecurityProperties(
    val issuer: String,
    val secret: String,
    val expireHours: Long,
    val adminExpireHours: Long
)
