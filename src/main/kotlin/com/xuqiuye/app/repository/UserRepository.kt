package com.xuqiuye.app.repository

import com.xuqiuye.app.authentication.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findUserByEmail(email: String): User?
}
