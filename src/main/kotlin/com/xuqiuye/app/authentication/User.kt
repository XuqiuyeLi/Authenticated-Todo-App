package com.xuqiuye.app.authentication

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "_users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @NotBlank
    @Column(name = "firstName", nullable = false)
    private var firstName: String = "",

    @NotBlank
    @Column(name = "lastName", nullable = false)
    private var lastName: String = "",

    @NotBlank
    @Column(name = "email", nullable = false)
    private var email: String,

    @NotBlank
    @Column(name = "password", nullable = false)
    private var password: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private var role: Role

) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        // user can only have a single role
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getUsername(): String {
        return email
    }

    override fun getPassword(): String {
        return password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }


    // Standard getters and setters if needed
}