package com.xuqiuye.app.service

import com.xuqiuye.app.authentication.AuthRequest
import com.xuqiuye.app.authentication.AuthResponse
import com.xuqiuye.app.authentication.RegisterRequest
import com.xuqiuye.app.exception.UserEmailAlreadyExistedException
import com.xuqiuye.app.repository.UserRepository
import com.xuqiuye.app.user.Role
import com.xuqiuye.app.user.User
import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface AuthService {
    fun register(request: RegisterRequest): AuthResponse
    fun authenticate(request: AuthRequest): AuthResponse
}

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) : AuthService {

    @Transactional
    override fun register(request: RegisterRequest): AuthResponse {
        val newUser = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = Role.USER
        )
        checkIfUserEmailExisted(request.email)
        userRepository.save(newUser)
        val jwtToken = jwtService.generateToken(userDetails = newUser)
        return AuthResponse(jwtToken)
    }

    override fun authenticate(request: AuthRequest): AuthResponse {
        // it will authenticate the user or throw an exception
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )
        val user = userRepository.findUserByEmail(request.email)
            ?: throw UsernameNotFoundException("User not found")

        val accessToken = jwtService.generateToken(userDetails = user)
        return AuthResponse(accessToken)
    }

    private fun checkIfUserEmailExisted(email: String) {
        val user = userRepository.findUserByEmail(email)
        if(user != null) {
            throw UserEmailAlreadyExistedException("User email already registered")
        }
    }
}