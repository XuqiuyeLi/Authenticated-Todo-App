package com.xuqiuye.app.service

import com.xuqiuye.app.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
    private val secretKey = Keys.hmacShaKeyFor(
        Decoders.BASE64.decode(jwtProperties.secretKey)
    )

    fun extractUsername(jwtToken: String): String? {
        return extractClaim(jwtToken) { obj: Claims -> obj.subject }
    }

    fun generateToken(
        extraClaims: Map<String, Any> = emptyMap(),
        userDetails: UserDetails
    ): String {
        return buildToken(extraClaims, userDetails, jwtProperties.accessTokenExpiration)
    }

    fun generateRefreshToken(
        userDetails: UserDetails
    ): String {
        return buildToken(userDetails = userDetails, expiration = jwtProperties.refreshTokenExpiration)
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
