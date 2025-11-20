package com.droidnotes.core.network.util

import com.droidnotes.common.AppResult
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
                    AppResult.Error(Exception("Error transforming response: ${e.message}", e))
                }
            } else {
                AppResult.Error(Exception("Response body is null"))
            }
        } else {
            val errorMessage = response.errorBody()?.string() 
                ?: response.message() 
                ?: "Unknown error"
            AppResult.Error(
                Exception("API Error: ${response.code()} - $errorMessage")
            )
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
            AppResult.Error(e)
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

