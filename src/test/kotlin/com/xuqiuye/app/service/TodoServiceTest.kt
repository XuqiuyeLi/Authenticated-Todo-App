package com.xuqiuye.app.service

import com.xuqiuye.app.model.Priority
import com.xuqiuye.app.model.Subtask
import com.xuqiuye.app.model.Todo
import com.xuqiuye.app.repository.TodoRepository
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoServiceTest {
    @Autowired
    private lateinit var todoRepo: TodoRepository
    private lateinit var todoService: TodoService

    @BeforeEach
    fun setup() {
        todoService = TodoServiceImpl(todoRepo = todoRepo)
        mockkStatic(Clock::class)

        val defaultClock = ZonedDateTime.of(2023, 1, 1, 1, 1, 0, 0, ZoneId.of("Asia/Tokyo")).toInstant()
        setFixedDateTimeToSystemClock(defaultClock)
        todoRepo.deleteAll()
    }

    @AfterEach
    fun clean() {
        unmockkStatic(Clock::class)
    }

    @Test
    fun `getTodos() will return all todos in database in order of most recently created`() {
        val sep11 = ZonedDateTime.of(2023, 9, 11, 9, 9, 9, 0, ZoneId.of("UTC")).toInstant()
        val nov11 = ZonedDateTime.of(2023, 11, 11, 9, 9, 10, 0, ZoneId.of("UTC")).toInstant()
        val oct21 = ZonedDateTime.of(2023, 10, 21, 20, 20, 21, 0, ZoneId.of("UTC")).toInstant()

        setFixedDateTimeToSystemClock(sep11)

        val firstTodo = todoService.createTodo(Todo(name = "Go to supermarket"))
        setFixedDateTimeToSystemClock(nov11)

        val secondTodo = todoService.createTodo(Todo(name = "Buy a new iphone"))

        setFixedDateTimeToSystemClock(oct21)

        val thirdTodo = todoService.createTodo(Todo(name = "Cook lamb as dinner", completeStatus = true))

        val todos = todoService.getTodos()

        assertThat(todos.size, equalTo(3))

        assertThat(todos[0].id, equalTo(secondTodo.id))
        assertThat(todos[0].createdAt, equalTo(LocalDateTime.of(2023, 11, 11, 18, 9, 10)))
        assertThat(todos[0].name, equalTo("Buy a new iphone"))

        assertThat(todos[1].id, equalTo(thirdTodo.id))
        assertThat(todos[1].createdAt, equalTo(LocalDateTime.of(2023, 10, 22, 5, 20, 21)))
        assertThat(todos[1].name, equalTo("Cook lamb as dinner"))
        assertThat(todos[1].completeStatus, equalTo(true))

        assertThat(todos[2].id, equalTo(firstTodo.id))
        assertThat(todos[2].createdAt, equalTo(LocalDateTime.of(2023, 9, 11, 18, 9, 9)))
        assertThat(todos[2].name, equalTo("Go to supermarket"))
    }

    @Test
    fun `getTodo() will return a todo with certain id`() {
        val oct19 = ZonedDateTime.of(2023, 10, 19, 20, 20, 20, 0, ZoneId.of("Asia/Tokyo")).toInstant()
        setFixedDateTimeToSystemClock(oct19)

        val savedNote = todoService.createTodo(
            Todo(
                id = 0,
                name = "Play tennis",
                priority = Priority.MEDIUM,
                subtasks = mutableListOf(
                    Subtask("book a tennis court")
                )
            )
        )

        val result = todoService.getTodo(savedNote.id)

        assertThat(result, equalTo(savedNote))
    }

    @Test
    fun `updateTodo() will return an updated todo with same id`() {
        val oct19 = ZonedDateTime.of(2023, 10, 19, 20, 20, 20, 0, ZoneId.of("Asia/Tokyo")).toInstant()
        setFixedDateTimeToSystemClock(oct19)
        val originalTodo = Todo(
            id = 0,
            name = "Play tennis",
            priority = Priority.MEDIUM,
            subtasks = mutableListOf(
                Subtask("book a tennis court")
            )
        )
        val todoForUpdate = Todo(
            name = "Play tennis with friends",
            priority = Priority.HIGH,
            subtasks = mutableListOf(
                Subtask("check friends' schedule"),
                Subtask("book a tennis court")
            )
        )

        todoService.createTodo(Todo())
        val secondSavedNote = todoService.createTodo(originalTodo)

        val result = todoService.updateTodo(secondSavedNote.id, todoForUpdate)

        assertThat(result.id, equalTo(secondSavedNote.id))
        assertThat(result.name, equalTo(todoForUpdate.name))
        assertThat(result.priority, equalTo(todoForUpdate.priority))
        assertThat(result.completeStatus, equalTo(secondSavedNote.completeStatus))
        assertThat(result.subtasks, equalTo(todoForUpdate.subtasks))
    }

    @Test
    fun `deleteTodo() will delete a todo with certain id`() {
        val oct19 = ZonedDateTime.of(2023, 10, 19, 20, 20, 20, 0, ZoneId.of("Asia/Tokyo")).toInstant()
        setFixedDateTimeToSystemClock(oct19)

        todoService.createTodo(Todo())
        val secondSavedNote = todoService.createTodo(
            Todo(
                id = 0,
                name = "Play tennis",
                priority = Priority.MEDIUM,
                subtasks = mutableListOf(
                    Subtask("book a tennis court")
                )
            )
        )

        val result = todoService.deleteTodo(secondSavedNote.id)

        assertThat(result, equalTo(secondSavedNote))
    }

    private fun setFixedDateTimeToSystemClock(now: Instant) {
        every { Instant.now() } returns now
    }
}
