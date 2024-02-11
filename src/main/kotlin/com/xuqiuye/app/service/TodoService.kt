package com.xuqiuye.app.service

import com.xuqiuye.app.exception.NotFoundException
import com.xuqiuye.app.model.Todo
import com.xuqiuye.app.repository.TodoRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface TodoService {
    fun getTodos(): List<Todo>
    fun getTodo(id: Long): Todo
    fun createTodo(newTodo: Todo): Todo
    fun updateTodo(id: Long, newTodo: Todo): Todo
    fun deleteTodo(id: Long): Todo
}

@Service
class TodoServiceImpl(
    private val todoRepo: TodoRepository
) : TodoService {
    @Transactional
    override fun getTodos(): List<Todo> {
        return todoRepo.findAllByOrderByCreatedAtDesc()
    }

    @Transactional
    override fun getTodo(id: Long): Todo {
        val todo = todoRepo.findByIdOrNull(id)
        if (todo != null) {
            return todo
        } else {
            throw NotFoundException("Could not find a todo by id: $id")
        }
    }

    @Transactional
    override fun createTodo(newTodo: Todo): Todo {
        newTodo.apply {
            this.createdAt = LocalDateTime.now()
        }
        return todoRepo.save(newTodo)
    }

    @Transactional
    override fun updateTodo(id: Long, newTodo: Todo): Todo {
        val originalTodo = this.getTodo(id)
        originalTodo.let {
            it.name = newTodo.name
            it.priority = newTodo.priority
            it.completeStatus = newTodo.completeStatus
            it.subtasks = newTodo.subtasks
        }

        return todoRepo.save(originalTodo)
    }

    @Transactional
    override fun deleteTodo(id: Long): Todo {
        val todoForDelete = this.getTodo(id)
        todoRepo.deleteById(id)
        println(todoForDelete)
        return todoForDelete
    }
}
