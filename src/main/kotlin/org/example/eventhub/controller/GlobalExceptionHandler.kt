package org.example.eventhub.controller

import org.example.eventhub.exception.EventAlreadyExistsException
import org.example.eventhub.exception.EventNotFoundException
import org.example.eventhub.exception.NoAccessException
import org.example.eventhub.exception.NoAvailableTicketsException
import org.example.eventhub.exception.OrderNotFoundException
import org.example.eventhub.exception.TicketNotFoundException
import org.example.eventhub.exception.UserAlreadyExistsException
import org.example.eventhub.exception.UserNotFoundException
import org.example.eventhub.model.AppError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun userNotFound(ex: UserNotFoundException) =
        error(ex.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun userAlreadyExists(ex: UserAlreadyExistsException) =
        error(ex.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(EventNotFoundException::class)
    fun eventNotFound(ex: EventNotFoundException) =
        error(ex.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(EventAlreadyExistsException::class)
    fun eventAlreadyExists(ex: EventAlreadyExistsException) =
        error(ex.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(NoAvailableTicketsException::class)
    fun noAvailableTickets(ex: NoAvailableTicketsException) =
        error(ex.message, HttpStatus.CONFLICT)

    @ExceptionHandler(OrderNotFoundException::class)
    fun orderNotFound(ex: OrderNotFoundException) =
        error(ex.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(TicketNotFoundException::class)
    fun ticketNotFound(ex: TicketNotFoundException) =
        error(ex.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(NoAccessException::class)
    fun noAccess(ex: NoAccessException) =
        error(ex.message, HttpStatus.FORBIDDEN)


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(ex: MethodArgumentNotValidException): ResponseEntity<AppError> {
        val details = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "invalid value")
        }

        return ResponseEntity(
            AppError(
                message = "Validation failed",
                status = HttpStatus.BAD_REQUEST.value(),
                details = details
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun invalidJson(): ResponseEntity<AppError> =
        error("Некорректное тело запроса", HttpStatus.BAD_REQUEST)


    @ExceptionHandler(Exception::class)
    fun unexpected(): ResponseEntity<AppError> =
        error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)


    private fun error(message: String?, status: HttpStatus): ResponseEntity<AppError> =
        ResponseEntity(
            AppError(
                message = message ?: status.reasonPhrase,
                status = status.value()
            ),
            status
        )
}