package com.tfg.veilapi.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now().toString(),
            status = ex.statusCode.value(),
            error = ex.statusCode.toString(),
            message = ex.reason ?: "Error occurred"
        )
        return ResponseEntity(errorResponse, ex.statusCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.toString(),
            message = errors
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        val message = when (ex.cause) {
            is SQLIntegrityConstraintViolationException -> {
                val sqlEx = ex.cause as SQLIntegrityConstraintViolationException
                when {
                    sqlEx.message?.contains("Duplicate entry") == true -> "A record with this ID already exists"
                    sqlEx.message?.contains("foreign key constraint fails") == true -> "Cannot delete or update a parent row: a foreign key constraint fails"
                    else -> sqlEx.message ?: "Database constraint violation"
                }
            }

            else -> "Database integrity constraint violation"
        }

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now().toString(),
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.toString(),
            message = message
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now().toString(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            message = ex.message ?: "Unexpected error occurred"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

data class ErrorResponse(
    val timestamp: String, val status: Int, val error: String, val message: String
)