package com.xuqiuye.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity
@Table(name = "todo")
data class Todo(
    @JsonProperty("id")
    @Id
    @GeneratedValue(generator = "todo_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "todo_sequence", sequenceName = "todo_sequence", allocationSize = 1)
    val id: Long = 0,

    @JsonProperty("name")
    @NotBlank
    @Column(name = "name", nullable = false)
    var name: String = "",

    @JsonProperty("completeStatus")
    @Column(name = "completeStatus", nullable = false)
    var completeStatus: Boolean = false,

    @JsonProperty("priority")
    @NotNull
    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW,

    @JsonIgnore
    @Column(name = "createdAt")
    var createdAt: LocalDateTime? = null,

    @JsonProperty("subtasks")
    @ElementCollection
    @CollectionTable(name = "subtasks", joinColumns = [JoinColumn(name = "todo_id")])
    @Column(name = "subtask")
    var subtasks: MutableList<Subtask> = mutableListOf()
)
