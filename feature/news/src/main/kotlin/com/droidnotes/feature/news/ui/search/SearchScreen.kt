package com.droidnotes.feature.news.ui.search

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
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
import com.droidnotes.feature.news.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search News") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchTextField(
                query = query,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            SearchResultsPagingList(
                viewModel = viewModel,
                onArticleClick = onArticleClick,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
    }
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search for news...") },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun SearchResultsPagingList(
    viewModel: SearchViewModel,
    onArticleClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val articlesPagingItems = viewModel.articlesPagingFlow.collectAsLazyPagingItems()
    val loadState = articlesPagingItems.loadState
    val query by viewModel.searchQuery.collectAsState()

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

    when {
        // Show placeholder when no search query
        query.isBlank() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Enter a search query to find articles",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
                        text = "Unable to search articles",
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
                Text(
                    text = "No articles found for \"$query\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

                // Show error at the bottom when append fails
                if (loadState.append is LoadState.Error) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = { articlesPagingItems.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}
