package com.xuqiuye.app.config

import com.xuqiuye.app.repository.TokenRepository
import com.xuqiuye.app.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val tokenRepository: TokenRepository
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        // this is the header that contains bearer token
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        // extract out the JWT token
        val jwtTokenStartIndex = 7
        val jwtToken: String = authHeader.substring(jwtTokenStartIndex)

        // extract out the user email
        val userEmail: String? = jwtService.extractUsername(jwtToken)
        // if use has not been authenticated
        updateSecurityContextIfUserIsNotAuthenticatedYet(userEmail, jwtToken, request)
    }

    private fun updateSecurityContextIfUserIsNotAuthenticatedYet(
        userEmail: String?,
        jwtToken: String,
        request: HttpServletRequest
    ) {
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(userEmail)
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                val authToken: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
    }
}
