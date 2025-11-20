package com.droidnotes.core.ui.error

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.droidnotes.common.AppResult
import com.droidnotes.common.exceptions.AppException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ErrorHandler {

    private const val TAG = "ErrorHandler"

    fun handleError(
        error: AppResult.Error,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        onRetry: (() -> Unit)? = null
    ) {
        when (val exception = error.exception) {
            is AppException.NetworkException -> {
                Log.e(TAG, "Network error: ${exception.message}", exception)
                showNetworkErrorSnackbar(snackbarHostState, scope, onRetry)
            }
            is AppException.TimeoutException -> {
                Log.e(TAG, "Timeout error: ${exception.message}", exception)
                showRetrySnackbar(
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    message = "Request timed out",
                    onRetry = onRetry
                )
            }
            is AppException.ServerException -> {
                Log.e(TAG, "Server error: ${exception.code} - ${exception.message}", exception)
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    message = "Server error. Please try again later"
                )
            }
            is AppException.AuthException -> {
                Log.e(TAG, "Auth error: ${exception.message}", exception)
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    message = "Authentication failed"
                )
            }
            is AppException.NotFoundException -> {
                Log.e(TAG, "Not found error: ${exception.message}", exception)
            }
            is AppException.ParseException -> {
                Log.e(TAG, "Parse error: ${exception.message}", exception)
            }
            is AppException.UnknownException -> {
                Log.e(TAG, "Unknown error: ${exception.message}", exception)
            }
        }
    }

    private fun showNetworkErrorSnackbar(
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        onRetry: (() -> Unit)?
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "No internet connection",
                actionLabel = if (onRetry != null) "Retry" else null,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed && onRetry != null) {
                onRetry()
            }
        }
    }

    private fun showRetrySnackbar(
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        message: String,
        onRetry: (() -> Unit)?
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (onRetry != null) "Retry" else null,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed && onRetry != null) {
                onRetry()
            }
        }
    }

    private fun showSnackbar(
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        message: String
    ) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}

