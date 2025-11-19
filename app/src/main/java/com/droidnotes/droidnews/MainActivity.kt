package com.droidnotes.droidnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.droidnotes.core.ui.AppTheme
import com.droidnotes.feature.news.NewsRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var initialArticleId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle deep links and notification intents
        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NewsRoot(
                        initialArticleId = initialArticleId.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                // Handle deep link
                intent.data?.let { uri ->
                    val articleId = extractArticleIdFromUri(uri)
                    initialArticleId.value = articleId
                }
            }
            else -> {
                // Handle notification intent
                val articleId = intent.getStringExtra("navigate_to_article")
                initialArticleId.value = articleId
            }
        }
    }

    private fun extractArticleIdFromUri(uri: Uri): String? {
        // Expected format: https://droidnews.app/article/{articleId}
        val pathSegments = uri.pathSegments
        return if (pathSegments.size >= 2 && pathSegments[0] == "article") {
            pathSegments[1]
        } else {
            null
        }
    }
}