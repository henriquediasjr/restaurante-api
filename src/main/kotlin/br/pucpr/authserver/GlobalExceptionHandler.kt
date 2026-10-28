package br.pucpr.authserver

import jakarta.validation.ConstraintViolationException
import org.springframework.beans.TypeMismatchException
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import tools.jackson.databind.DatabindException
import tools.jackson.databind.exc.InvalidFormatException

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = ex.bindingResult.fieldErrors
            .groupBy({ it.field }, { it.defaultMessage ?: "Invalid value" })

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, "Invalid request body")
        problemDetail.setProperty("errors", errors)

        return createResponseEntity(problemDetail, headers, HttpStatus.UNPROCESSABLE_CONTENT, request)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = mutableMapOf<String, List<String>>()
        val cause = ex.cause

        if (cause is DatabindException) {
            val field = cause.path.joinToString(".") { it.propertyName ?: "[${it.index}]" }
            val message = if (cause is InvalidFormatException) {
                "Invalid type. Expected ${cause.targetType.simpleName}"
            } else {
                "Invalid field format"
            }
            errors[field] = listOf(message)
        } else {
            errors["body"] = listOf("Malformed JSON request")
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid JSON request")
        problemDetail.setProperty("errors", errors)

        return createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request)
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = if (ex is MethodArgumentTypeMismatchException) {
            mapOf(ex.name to listOf("Invalid type. Expected ${ex.requiredType?.simpleName}"))
        } else {
            mapOf("parameter" to listOf("Invalid type"))
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid URL parameter")
        problemDetail.setProperty("errors", errors)

        return createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        ex: ConstraintViolationException
    ): ResponseEntity<ProblemDetail> {
        val errors = ex.constraintViolations
            .groupBy({ it.propertyPath.toString().substringAfterLast('.') }, { it.message })

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, "Validation failed")
        problemDetail.setProperty("errors", errors)

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(problemDetail)
    }

    /* Habilitar com o Spring Security
        @ExceptionHandler(AuthenticationException::class)
        fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<ProblemDetail> {
            val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Unauthorized")
            problemDetail.setProperty("errors", mapOf("detail" to listOf(ex.message ?: "Authentication failed")))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail)
        }

        @ExceptionHandler(AccessDeniedException::class)
        fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ProblemDetail> {
            val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Forbidden")
            problemDetail.setProperty("errors", mapOf("detail" to listOf(ex.message ?: "Access denied")))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail)
        }*/

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDatabaseConstraints(ex: DataIntegrityViolationException): ResponseEntity<ProblemDetail> {
        return handle500(ex)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val status = extractStatus(ex)

        if (status == null || status.is5xxServerError) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(handle500(ex).body)
        }

        val message = ex.message.takeUnless { it.isNullOrBlank() }
            ?: HttpStatus.valueOf(status.value()).reasonPhrase

        val problemDetail = ProblemDetail.forStatusAndDetail(status, message)

        val details = extractDetails(ex)
        if (details.isNotEmpty()) {
            problemDetail.setProperty("errors", details)
        }

        return createResponseEntity(problemDetail, HttpHeaders.EMPTY, status, request)
    }

    private fun handle500(ex: Exception): ResponseEntity<ProblemDetail> {
        logger.error("Internal Server Error: ", ex)
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail)
    }

    private fun extractStatus(ex: Exception): HttpStatusCode? {
        try {
            val method = ex.javaClass.getMethod("getStatusCode")
            (method.invoke(ex) as? HttpStatusCode)?.let { return it }
        } catch (_: Exception) {
        }

        val annotation = AnnotatedElementUtils.findMergedAnnotation(ex.javaClass, ResponseStatus::class.java)
        return annotation?.value
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractDetails(ex: Exception): Map<String, List<String>> {
        try {
            val getBodyMethod = ex.javaClass.getMethod("getBody")
            val body = getBodyMethod.invoke(ex)
            if (body is ProblemDetail) {
                return body.properties?.mapValues { listOf(it.value.toString()) } ?: emptyMap()
            }
        } catch (_: Exception) {
        }

        try {
            val getDetailsMethod = ex.javaClass.getMethod("getDetails")
            val rawDetails = getDetailsMethod.invoke(ex)
            if (rawDetails is Map<*, *>) {
                return rawDetails as Map<String, List<String>>
            }
        } catch (_: Exception) {
        }

        return emptyMap()
    }
}