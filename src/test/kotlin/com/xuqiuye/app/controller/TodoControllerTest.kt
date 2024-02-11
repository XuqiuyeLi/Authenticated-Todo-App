package com.xuqiuye.app.controller

import com.xuqiuye.app.model.Priority
import com.xuqiuye.app.model.Subtask
import com.xuqiuye.app.model.Todo
import com.xuqiuye.app.service.TodoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TodoControllerTest {

    private val mockedTodoService = mockk<TodoService>()
    private lateinit var mockMvc: MockMvc

    val mockedTodo = Todo(
        name = "Saturday Board Game Night",
        priority = Priority.MEDIUM,
        subtasks = mutableListOf(
            Subtask(subtaskName = "Buy food and drink"),
            Subtask(subtaskName = "Decide which friends to invite")
        )
    )

    private val mockedTodoWithId1 = Todo(
        id = 1,
        name = "Plan a trip",
        subtasks = mutableListOf(
            Subtask(subtaskName = "Decide where to go"),
            Subtask(subtaskName = "Check flights and hotels"),
            Subtask(subtaskName = "Reserve the dates")
        )
    )

    private val mockedTodoWithId2 = Todo(
        id = 2,
        name = "Buy egg and milk",
        priority = Priority.HIGH,
        completeStatus = true
    )

    @BeforeEach
    fun setup() {
        mockMvc = buildController()
    }

    @Nested
    @DisplayName("/api/note [POST]")
    inner class ApiTodoPostTest {
        @Test
        fun `createTodo() returns status 201 if successfully create todo`() {
            every { mockedTodoService.createTodo(any()) } returns mockedTodo

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/todos")
                    .content(
                        """
                        {
                            "name": "Saturday Board Game Night",
                            "priority": "MEDIUM",
                            "subtasks": [
                                {
                                    "subtaskName": "Buy food and drink"
                                },
                                {
                                    "subtaskName": "Decide which friends to invite"
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isCreated)

            verify {
                mockedTodoService.createTodo(
                    Todo(
                        name = "Saturday Board Game Night",
                        priority = Priority.MEDIUM,
                        subtasks = mutableListOf(
                            Subtask(subtaskName = "Buy food and drink"),
                            Subtask(subtaskName = "Decide which friends to invite")
                        )
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("/api/note [GET]")
    inner class ApiTodoGetTest {
        @Test
        fun `getTodos() returns status 200 and the list of todos`() {
            every { mockedTodoService.getTodos() } returns mutableListOf(mockedTodoWithId1, mockedTodoWithId2)

            mockMvc.perform(
                get("/api/v1/todos")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plan a trip"))
                .andExpect(jsonPath("$[0].priority").value("LOW"))
                .andExpect(jsonPath("$[0].completeStatus").value(false))
                .andExpect(jsonPath("$[0].subtasks[0].subtaskName").value("Decide where to go"))
                .andExpect(jsonPath("$[0].subtasks[1].subtaskName").value("Check flights and hotels"))
                .andExpect(jsonPath("$[0].subtasks[2].subtaskName").value("Reserve the dates"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Buy egg and milk"))
                .andExpect(jsonPath("$[1].priority").value("HIGH"))
                .andExpect(jsonPath("$[1].completeStatus").value(true))
        }

        @Test
        fun `getTodo() returns status 200 and the todo by id`() {
            every { mockedTodoService.getTodo(1) } returns mockedTodoWithId1

            mockMvc.perform(
                get("/api/v1/todos/1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Plan a trip"))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.completeStatus").value(false))
                .andExpect(jsonPath("$.subtasks[0].subtaskName").value("Decide where to go"))
                .andExpect(jsonPath("$.subtasks[1].subtaskName").value("Check flights and hotels"))
                .andExpect(jsonPath("$.subtasks[2].subtaskName").value("Reserve the dates"))
        }
    }

    @Nested
    @DisplayName("/api/note [UPDATE]")
    inner class ApiTodoUpdateTest {
        @Test
        fun `updateTodo() returns status 200 and update the todo by id if exists`() {
            val updatedTodo = Todo(
                id = 999,
                name = "Saturday Board Game Night Update to Sunday",
                completeStatus = true,
                priority = Priority.MEDIUM,
                subtasks = mutableListOf(
                    Subtask(subtaskName = "Buy food and drink"),
                    Subtask(subtaskName = "Decide which friends to invite"),
                    Subtask(subtaskName = "Tidy up after finish")
                )
            )

            every { mockedTodoService.updateTodo(999, any()) } returns updatedTodo

            mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/todos/999")
                    .content(
                        """
                        {
                            "name": "Saturday Board Game Night Update to Sunday",
                            "completeStatus": true,
                            "priority": "MEDIUM",
                            "subtasks": [
                                {
                                    "subtaskName": "Buy food and drink"
                                },
                                {
                                    "subtaskName": "Decide which friends to invite"
                                },
                                {
                                    "subtaskName": "Tidy up after finish"
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.name").value("Saturday Board Game Night Update to Sunday"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.completeStatus").value(true))
                .andExpect(jsonPath("$.subtasks[0].subtaskName").value("Buy food and drink"))
                .andExpect(jsonPath("$.subtasks[1].subtaskName").value("Decide which friends to invite"))
                .andExpect(jsonPath("$.subtasks[2].subtaskName").value("Tidy up after finish"))
        }
    }

    @Nested
    @DisplayName("/api/note [DELETE]")
    inner class ApiTodoDeleteTest {
        @Test
        fun `deleteTodo() returns status 200 and delete the todo by id if exists`() {
            every { mockedTodoService.deleteTodo(1) } returns mockedTodoWithId1

            mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/todos/1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Plan a trip"))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.completeStatus").value(false))
                .andExpect(jsonPath("$.subtasks[0].subtaskName").value("Decide where to go"))
                .andExpect(jsonPath("$.subtasks[1].subtaskName").value("Check flights and hotels"))
                .andExpect(jsonPath("$.subtasks[2].subtaskName").value("Reserve the dates"))
        }
    }

    private fun buildController(): MockMvc {
        return MockMvcBuilders
            .standaloneSetup(TodoController(mockedTodoService))
            .build()
    }
}
