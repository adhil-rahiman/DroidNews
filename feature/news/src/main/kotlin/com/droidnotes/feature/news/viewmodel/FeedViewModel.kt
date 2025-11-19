package com.droidnotes.feature.news.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import coil3.util.CoilUtils.result
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.domain.news.PagedNewsRepository
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import com.droidnotes.feature.news.ui.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val pagedNewsRepository: PagedNewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val articlesPagingFlow: Flow<PagingData<Article>> = _uiState
        .flatMapLatest { state ->
            Log.d("TestLogs", "articlesPagingFlow: state: ${state}")
            pagedNewsRepository.topHeadlines(state.selectedCategory)
        }
        .cachedIn(viewModelScope)

    init {
        // Initialize with default category (null for all)
        selectCategory(null)
    }

    fun selectCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setRefreshing(isRefreshing: Boolean) {
        _uiState.update { it.copy(isRefreshing = isRefreshing) }
        if (!isRefreshing) {
            // Reset after a short delay to allow UI to show the refresh indicator
            viewModelScope.launch {
                kotlinx.coroutines.delay(500)
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            newsRepository.toggleBookmark(articleId)
        }
    }
}
