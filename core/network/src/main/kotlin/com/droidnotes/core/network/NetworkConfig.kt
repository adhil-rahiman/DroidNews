package com.droidnotes.core.network

object NetworkConstant {
    const val GNEWS_API_BASE_URL = "https://gnews.io/api/v4/"
}

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 10,
    val readTimeoutSeconds: Long = 20,
)