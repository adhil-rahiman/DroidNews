package com.droidnotes.core.network

/** Provides API keys or tokens from a secure source (e.g., Gradle Secrets or keystore). */
fun interface ApiKeyProvider {
    fun getApiKey(alias: String): String?
}

data class NetworkConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 10,
    val readTimeoutSeconds: Long = 20,
)
