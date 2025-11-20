package com.droidnotes.common.exceptions

sealed class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    data class NetworkException(
        override val message: String = "No internet connection",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    data class ServerException(
        val code: Int,
        override val message: String = "Server error: $code",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    data class AuthException(
        override val message: String = "Authentication failed",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    data class TimeoutException(
        override val message: String = "Request timed out",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    data class ParseException(
        override val message: String = "Failed to parse response",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    data class NotFoundException(
        override val message: String = "Resource not found",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    data class UnknownException(
        override val message: String = "An unknown error occurred",
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    fun toUserMessage(): String {
        return when (this) {
            is NetworkException -> "Please check your internet connection"
            is ServerException -> when (code) {
                500 -> "Server is experiencing issues. Please try again later"
                503 -> "Service is temporarily unavailable"
                else -> "Something went wrong. Please try again"
            }
            is AuthException -> "Authentication failed. Please log in again"
            is TimeoutException -> "Request took too long. Please try again"
            is ParseException -> "Failed to process data. Please try again"
            is NotFoundException -> "Requested content not found"
            is UnknownException -> message ?: "Something went wrong"
        }
    }
}

