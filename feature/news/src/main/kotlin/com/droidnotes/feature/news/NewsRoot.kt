package com.droidnotes.feature.news

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.droidnotes.feature.news.navigation.NewsNavGraph
import com.droidnotes.feature.news.navigation.NewsRoutes

@Composable
fun NewsRoot(
    initialArticleId: String? = null,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Handle initial deep link navigation
    initialArticleId?.let { articleId ->
        LaunchedEffect(articleId) {
            navController.navigate(NewsRoutes.articleDetail(articleId)) {
                popUpTo(NewsRoutes.FEED) { inclusive = false }
            }
        }
    }

    NewsNavGraph(
        navController = navController,
        modifier = modifier.fillMaxSize()
    )
}
