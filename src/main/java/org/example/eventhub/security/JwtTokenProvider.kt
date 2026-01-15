package org.example.eventhub.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider {

    private val secretKey = Keys.hmacShaKeyFor(
        "secret".toByteArray()
    )

    private val validityInMs = 60 * 60 * 1000

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

    fun getUsername(token: String): String =
        Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .payload.subject

    fun getAuthorities(token: String): List<String> =
        Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .payload["roles"] as List<String>
}