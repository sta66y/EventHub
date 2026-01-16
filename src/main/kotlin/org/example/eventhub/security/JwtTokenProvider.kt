package org.example.eventhub.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)

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
        log.debug("Генерация JWT токена...")

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
        } catch (e: ExpiredJwtException) {
            false
        } catch (e: JwtException) {
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