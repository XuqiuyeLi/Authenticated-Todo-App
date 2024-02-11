package com.xuqiuye.app.controller

import com.xuqiuye.app.authentication.AuthRequest
import com.xuqiuye.app.authentication.AuthResponse
import com.xuqiuye.app.authentication.RegisterRequest
import com.xuqiuye.app.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody request: AuthRequest): AuthResponse {
        return authService.authenticate(request)
    }
}