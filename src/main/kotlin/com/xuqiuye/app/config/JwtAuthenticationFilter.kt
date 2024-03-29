package com.xuqiuye.app.config

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
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        if (request.servletPath.contains("/api/v1/auth")) {
            filterChain.doFilter(request, response)
            return
        }
        // this is the header that contains bearer token
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        // extract out the JWT token
        val jwtToken: String = authHeader.substringAfter("Bearer ")

        // extract out the user email
        val userEmail: String? = jwtService.extractUsername(jwtToken)
        // if use has not been authenticated
        updateSecurityContextIfUserIsNotAuthenticatedYet(userEmail, jwtToken, request)
        // go to next request and response
        filterChain.doFilter(request, response)
    }

    private fun updateSecurityContextIfUserIsNotAuthenticatedYet(
        userEmail: String?,
        jwtToken: String,
        request: HttpServletRequest
    ) {
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(userEmail)
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
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
