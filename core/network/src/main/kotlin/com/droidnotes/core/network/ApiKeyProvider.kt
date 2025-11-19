package com.droidnotes.core.network

import javax.inject.Inject

/** Provides API keys or tokens from a secure source (e.g., Gradle Secrets or keystore). */
fun interface ApiKeyProvider {
    fun getApiKey(): String?
}

class BuildConfigApiKeyProvider @Inject constructor() : ApiKeyProvider {
    override fun getApiKey(): String? {
        return BuildConfig.API_BASE_URL.ifEmpty { null }
    }
}
