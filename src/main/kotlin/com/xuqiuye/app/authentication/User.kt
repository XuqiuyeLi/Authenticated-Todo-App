package com.xuqiuye.app.authentication

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "_user")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @NotBlank
    @Column(name = "firstName", nullable = false)
    val firstName: String = "",

    @NotBlank
    @Column(name = "lastName", nullable = false)
    val lastName: String = "",

    @NotBlank
    @Column(name = "email", nullable = false)
    val email: String = "",

    @NotBlank
    @Column(name = "password", nullable = false)
    val password: String = ""
)