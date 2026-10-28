package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.Date

@Component
class JWT(
    private val properties: SecurityProperties
) {
    private val key = Keys.hmacShaKeyFor(properties.secret.toByteArray())

    fun createToken(user: User): String {
        val roles = user.roles.map { it.name }.sorted()
        val hours = if ("ADMIN" in roles) properties.adminExpireHours else properties.expireHours
        val now = Date()
        return Jwts.builder()
            .issuer(properties.issuer)
            .subject(user.id.toString())
            .issuedAt(now)
            .expiration(Date(now.time + Duration.ofHours(hours).toMillis()))
            .claim("name", user.name)
            .claim("roles", roles)
            .signWith(key)
            .compact()
    }

    fun extract(token: String): Authentication? = try {
        val claims = Jwts.parser()
            .verifyWith(key)
            .requireIssuer(properties.issuer)
            .build()
            .parseSignedClaims(token)
            .payload
        val roles = (claims["roles"] as? List<*>)?.map { it.toString() } ?: emptyList()
        val principal = AuthUser(claims.subject.toLong(), claims["name"] as String, roles)
        UsernamePasswordAuthenticationToken(
            principal,
            null,
            roles.map { SimpleGrantedAuthority("ROLE_$it") }
        )
    } catch (e: Exception) {
        log.debug("Invalid JWT token: {}", e.message)
        null
    }

    companion object {
        private val log = LoggerFactory.getLogger(JWT::class.java)
    }
}
