package com.xuqiuye.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Embeddable

@Embeddable
data class Subtask(
    @JsonProperty("subtaskName")
    val subtaskName: String = "",

    @JsonProperty("completeStatus")
    val completeStatus: Boolean = false
)
