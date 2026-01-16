package org.example.eventhub.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.debug("JWT фильтр аутентификации вызван")

        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            log.debug("Запрос содержит заголовок с токеном")

            val token = header.substring(7)

            if (jwtTokenProvider.validate(token)) {
                log.debug("Токен прошел проверку")

                val email = jwtTokenProvider.getEmail(token)
                val userDetails = userDetailsService.loadUserByUsername(email)

                val auth = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )

                if (SecurityContextHolder.getContext().authentication == null) {
                    SecurityContextHolder.getContext().authentication = auth
                    log.debug("Факт аутентификации успешно загружен в secutiry context")
                }

            }
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.servletPath.startsWith("/api/v1/auth")

}
