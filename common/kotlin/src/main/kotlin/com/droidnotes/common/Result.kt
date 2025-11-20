package com.droidnotes.common

import com.droidnotes.common.exceptions.AppException

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val exception: AppException) : AppResult<Nothing>() {
        constructor(throwable: Throwable) : this(
            when (throwable) {
                is AppException -> throwable
                else -> AppException.UnknownException(
                    message = throwable.message ?: "Unknown error",
                    cause = throwable
                )
            }
        )
    }
}

inline fun <T> runCatchingResult(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (t: Throwable) {
    AppResult.Error(t)
}

fun AppResult.Error.getUserMessage(): String = exception.toUserMessage()
