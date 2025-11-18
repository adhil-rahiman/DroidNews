package com.droidnotes.core.analytics

interface Analytics {
    fun log(event: String, params: Map<String, Any?> = emptyMap())
}

class LoggerAnalytics : Analytics {
    override fun log(event: String, params: Map<String, Any?>) {
        // Placeholder; wire to Logcat or real provider later
    }
}
