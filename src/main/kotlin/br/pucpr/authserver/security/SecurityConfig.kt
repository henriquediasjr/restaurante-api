package br.pucpr.authserver.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenFilter: JwtTokenFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource() = UrlBasedCorsConfigurationSource().apply {
        registerCorsConfiguration("/**", CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
        })
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .cors { }
        .csrf { it.disable() }
        .headers { headers -> headers.frameOptions { it.sameOrigin() } }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests {
            it.requestMatchers(
                "/health", "/h2-console/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error"
            ).permitAll()
            it.requestMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()
            it.requestMatchers(HttpMethod.GET, "/products/**", "/categories/**", "/tags/**").permitAll()
            it.requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
            it.requestMatchers("/users/**", "/roles/**").hasRole("ADMIN")
            it.anyRequest().authenticated()
        }
        .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        .exceptionHandling { handling ->
            handling.authenticationEntryPoint { _, response, ex ->
                writeError(response, HttpStatus.UNAUTHORIZED, "Unauthorized", ex.message ?: "Authentication required")
            }
            handling.accessDeniedHandler { _, response, ex ->
                writeError(response, HttpStatus.FORBIDDEN, "Forbidden", ex.message ?: "Access denied")
            }
        }
        .build()

    private fun writeError(
        response: jakarta.servlet.http.HttpServletResponse,
        status: HttpStatus,
        title: String,
        detail: String
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.writer.write("""{"title":"$title","status":${status.value()},"detail":"${detail.replace("\"", "'")}"}""")
    }
}
