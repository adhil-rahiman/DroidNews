package com.droidnotes.feature.news.ui.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.droidnotes.common.AppResult
import com.droidnotes.core.ui.error.ErrorHandler
import com.droidnotes.feature.news.R
import com.droidnotes.feature.news.ui.components.ArticleCard
import com.droidnotes.feature.news.viewmodel.BookmarksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onArticleClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        val articlesPagingItems = viewModel.articlesPagingFlow.collectAsLazyPagingItems()
        val loadState = articlesPagingItems.loadState

        LaunchedEffect(loadState.refresh) {
            if (loadState.refresh is LoadState.Error) {
                val error = (loadState.refresh as LoadState.Error).error
                if (error is Exception) {
                    ErrorHandler.handleError(
                        error = AppResult.Error(error),
                        snackbarHostState = snackbarHostState,
                        scope = scope,
                        onRetry = { articlesPagingItems.retry() }
                    )
                }
            }
        }

        LaunchedEffect(loadState.append) {
            if (loadState.append is LoadState.Error) {
                val error = (loadState.append as LoadState.Error).error
                if (error is Exception) {
                    ErrorHandler.handleError(
                        error = AppResult.Error(error),
                        snackbarHostState = snackbarHostState,
                        scope = scope,
                        onRetry = { articlesPagingItems.retry() }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Loading state
                loadState.refresh is LoadState.Loading && articlesPagingItems.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // Error state
                loadState.refresh is LoadState.Error && articlesPagingItems.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Unable to load bookmarks",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { articlesPagingItems.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                // Empty state
                articlesPagingItems.itemCount == 0 && loadState.refresh is LoadState.NotLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No bookmarks yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bookmark articles to read them later",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                // Success state
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = articlesPagingItems.itemCount,
                            key = articlesPagingItems.itemKey { it.id },
                            contentType = articlesPagingItems.itemContentType { "article" }
                        ) { index ->
                            val article = articlesPagingItems[index]
                            if (article != null) {
                                ArticleCard(
                                    article = article,
                                    onArticleClick = onArticleClick,
                                    onBookmarkClick = { viewModel.toggleBookmark(article) }
                                )
                            }
                        }

                        // Show loading indicator at the bottom when appending
                        if (loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
