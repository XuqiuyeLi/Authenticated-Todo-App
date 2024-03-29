package com.xuqiuye.app.controller

import com.xuqiuye.app.model.Todo
import com.xuqiuye.app.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TodoController(val service: TodoService) {
    @GetMapping("/todos")
    fun getTodos(): List<Todo> {
        return service.getTodos()
    }

    @GetMapping("/todos/{id}")
    fun getTodo(@PathVariable id: String): Todo {
        return service.getTodo(id.toLong())
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTodo(@RequestBody newTodo: Todo): Todo {
        return service.createTodo(newTodo)
    }

    @PatchMapping("/todos/{id}")
    fun updateTodo(@PathVariable id: String, @RequestBody newTodo: Todo): Todo {
        return service.updateTodo(id.toLong(), newTodo)
    }

    @DeleteMapping("/todos/{id}")
    fun deleteTodo(@PathVariable id: String): Todo {
        return service.deleteTodo(id.toLong())
    }
}
