package com.xuqiuye.app.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST)
data class UserEmailAlreadyExistedException(override val message: String): RuntimeException()