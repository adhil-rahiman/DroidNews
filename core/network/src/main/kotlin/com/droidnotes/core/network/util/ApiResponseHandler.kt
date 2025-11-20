package com.droidnotes.core.network.util

import com.droidnotes.common.AppResult
import com.droidnotes.common.exceptions.AppException
import retrofit2.Response

object ApiResponseHandler {

    /**
     * Generic function to handle Retrofit Response and convert to AppResult
     *
     * @param response The Retrofit Response object
     * @param transform Lambda to transform the response body to desired type
     * @return AppResult with transformed data or error
     */
    fun <T, R> handleResponse(
        response: Response<T>,
        transform: (T) -> R
    ): AppResult<R> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                try {
                    val transformedData = transform(body)
                    AppResult.Success(transformedData)
                } catch (e: Exception) {
                    AppResult.Error(
                        AppException.ParseException(
                            message = "Error transforming response: ${e.message}",
                            cause = e
                        )
                    )
                }
            } else {
                AppResult.Error(
                    AppException.ParseException(message = "Response body is null")
                )
            }
        } else {
            val errorMessage = response.errorBody()?.string() 
                ?: response.message() 
                ?: "Unknown error"
            
            val exception = when (response.code()) {
                401, 403 -> AppException.AuthException(
                    message = "Authentication failed: ${response.code()}"
                )
                404 -> AppException.NotFoundException(
                    message = "Resource not found"
                )
                in 500..599 -> AppException.ServerException(
                    code = response.code(),
                    message = errorMessage
                )
                else -> AppException.UnknownException(
                    message = "API Error: ${response.code()} - $errorMessage"
                )
            }
            
            AppResult.Error(exception)
        }
    }

    /**
     * Simplified version when no transformation is needed
     */
    fun <T> handleResponse(response: Response<T>): AppResult<T> {
        return handleResponse(response) { it }
    }

    /**
     * Execute API call with automatic error handling
     *
     * @param apiCall Suspend function that makes the API call
     * @param transform Optional transformation of the response body
     * @return AppResult with data or error
     */
    suspend fun <T, R> executeApiCall(
        apiCall: suspend () -> Response<T>,
        transform: (T) -> R
    ): AppResult<R> {
        return try {
            val response = apiCall()
            handleResponse(response, transform)
        } catch (e: Exception) {
            val exception = when {
                e is java.net.SocketTimeoutException -> {
                    AppException.TimeoutException(cause = e)
                }
                e is java.net.UnknownHostException -> {
                    AppException.NetworkException(
                        message = "Unable to reach server",
                        cause = e
                    )
                }
                e is java.io.IOException -> {
                    AppException.NetworkException(
                        message = "Network error occurred",
                        cause = e
                    )
                }
                else -> {
                    AppException.UnknownException(
                        message = e.message ?: "Unknown error occurred",
                        cause = e
                    )
                }
            }
            AppResult.Error(exception)
        }
    }

    /**
     * Execute API call without transformation
     */
    suspend fun <T> executeApiCall(
        apiCall: suspend () -> Response<T>
    ): AppResult<T> {
        return executeApiCall(apiCall) { it }
    }
}

