package com.xuqiuye.app.repository

import com.xuqiuye.app.model.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Todo>
}
