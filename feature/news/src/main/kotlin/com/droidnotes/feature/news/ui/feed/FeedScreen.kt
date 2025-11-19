package com.droidnotes.feature.news.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.droidnotes.domain.news.model.Category
import com.droidnotes.feature.news.R
import com.droidnotes.feature.news.ui.components.ArticleCard
import com.droidnotes.feature.news.ui.components.CategoryChips
import com.droidnotes.feature.news.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onArticleClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = onBookmarksClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark_outline),
                            contentDescription = "Bookmarks"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CategoryChips(
                categories = Category.entries,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ArticlesPagingList(
                viewModel = viewModel,
                uiState = uiState,
                onArticleClick = onArticleClick
            )
        }
    }
}

@Composable
private fun ArticlesPagingList(
    viewModel: FeedViewModel,
    uiState: com.droidnotes.feature.news.ui.FeedUiState,
    onArticleClick: (String) -> Unit
) {
    val articlesPagingItems = viewModel.articlesPagingFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.selectedCategory) {
        articlesPagingItems.refresh()
        listState.scrollToItem(0)
    }

    val isRefreshing = articlesPagingItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { articlesPagingItems.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
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
                        onBookmarkClick = { articleId -> viewModel.toggleBookmark(articleId) }
                    )
                }
            }
        }
    }
}
