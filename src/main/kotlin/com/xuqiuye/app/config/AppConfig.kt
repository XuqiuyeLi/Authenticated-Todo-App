package com.xuqiuye.app.config

import com.xuqiuye.app.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException


@Configuration
class AppConfig(
    private val userRepository: UserRepository
) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            userRepository.findUserByEmail(username)
                ?: throw UsernameNotFoundException("User not found")
        }
    }
}