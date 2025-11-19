package com.droidnotes.feature.news.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.droidnotes.feature.news.ui.bookmarks.BookmarksScreen
import com.droidnotes.feature.news.ui.detail.ArticleDetailScreen
import com.droidnotes.feature.news.ui.feed.FeedScreen
import com.droidnotes.feature.news.ui.search.SearchScreen

@Composable
fun NewsNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NewsRoutes.FEED
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NewsRoutes.FEED) {
            FeedScreen(
                onArticleClick = { articleId ->
                    navController.navigate(NewsRoutes.articleDetail(articleId))
                },
                onSearchClick = {
                    navController.navigate(NewsRoutes.SEARCH)
                },
                onBookmarksClick = {
                    navController.navigate(NewsRoutes.BOOKMARKS)
                }
            )
        }

        composable(NewsRoutes.SEARCH) {
            SearchScreen(
                onArticleClick = { articleId ->
                    navController.navigate(NewsRoutes.articleDetail(articleId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NewsRoutes.ARTICLE_DETAIL,
            arguments = listOf(
                navArgument("articleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(
                articleId = articleId,
                onBackClick = {
                    navController.popBackStack()
                },
                onBookmarkClick = { /* Handle bookmark toggle */ }
            )
        }

        composable(NewsRoutes.BOOKMARKS) {
            BookmarksScreen(
                onArticleClick = { articleId ->
                    navController.navigate(NewsRoutes.articleDetail(articleId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
