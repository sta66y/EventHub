package org.example.eventhub.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    lateinit var secret: String

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    @Value("\${jwt.validity}")
    private var validityInMs: Long = 0

    fun generateToken(
        username: String,
        authorities: List<String>
    ): String {
       val now = Date()
       val expiry = Date(now.time + validityInMs)

       return Jwts.builder()
           .subject(username)
           .claim("roles", authorities)
           .issuedAt(now)
           .expiration(expiry)
           .signWith(secretKey)
           .compact()
    }

    fun validate(token: String): Boolean =
            try {
                Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                true
            } catch (e: Exception) {
                false
            }

    fun getEmail(token: String): String =
        Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .payload.subject

    fun getAuthorities(token: String): List<String> =
        Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .payload["roles"] as List<String>
}