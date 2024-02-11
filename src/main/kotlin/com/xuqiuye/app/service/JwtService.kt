package com.xuqiuye.app.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    // save key, jwtExpiration, refreshExpiration to env variables
    private val key = "NfrqXH5FLWlSK48GwBqqqU2lwjH0oK9X"
    private val jwtExpiration = 2000000L
    private val refreshExpiration = 3000000L
    private val secretKey = Keys.hmacShaKeyFor(
        key.toByteArray()
    )

    fun extractUsername(jwtToken: String): String {
        return extractClaim(jwtToken) { obj: Claims -> obj.subject }
    }

    fun generateToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails
    ): String {
        return buildToken(extraClaims, userDetails, jwtExpiration)
    }

    fun generateRefreshToken(
        userDetails: UserDetails
    ): String {
        return buildToken(userDetails = userDetails, expiration = refreshExpiration)
    }

    fun buildToken(
        additionalClaims: Map<String, Any> = emptyMap(),
        userDetails: UserDetails,
        expiration: Long
    ): String {
        return Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()
    }

    fun isTokenValid(jwtToken: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(jwtToken)
        return username == userDetails.username && !isTokenExpired(jwtToken)
    }

    fun isTokenExpired(jwtToken: String): Boolean {
        return extractExpiration(jwtToken).before(Date())
    }

    fun extractExpiration(jwtToken: String): Date {
        return extractClaim(jwtToken) { obj: Claims -> obj.expiration }
    }
    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()
        return parser
            .parseSignedClaims(token)
            .payload
    }
}
