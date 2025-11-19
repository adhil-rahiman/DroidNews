package com.droidnotes.feature.news.ui

import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category

sealed class NewsUiState {
    data object Loading : NewsUiState()
    data class Success(val data: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

sealed class ArticleDetailUiState {
    data object Loading : ArticleDetailUiState()
    data class Success(val article: Article) : ArticleDetailUiState()
    data class Error(val message: String) : ArticleDetailUiState()
}

data class FeedUiState(
    val selectedCategory: Category? = null,
    val articlesState: NewsUiState = NewsUiState.Loading,
    val isRefreshing: Boolean = false
)

data class SearchUiState(
    val query: String = "",
    val articlesState: NewsUiState = NewsUiState.Loading,
    val isSearching: Boolean = false
)

data class BookmarksUiState(
    val articlesState: NewsUiState = NewsUiState.Loading
)
