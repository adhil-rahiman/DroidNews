package com.droidnotes.core.ui.error

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.droidnotes.common.AppResult

@Composable
fun HandleError(
    error: AppResult.Error?,
    snackbarHostState: SnackbarHostState,
    onRetry: (() -> Unit)? = null,
    onErrorHandled: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(error) {
        error?.let {
            ErrorHandler.handleError(
                error = it,
                snackbarHostState = snackbarHostState,
                scope = scope,
                onRetry = onRetry
            )
            onErrorHandled()
        }
    }
}

