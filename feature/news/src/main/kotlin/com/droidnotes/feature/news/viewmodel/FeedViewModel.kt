package com.droidnotes.feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.domain.news.model.Category
import com.droidnotes.feature.news.ui.FeedUiState
import com.droidnotes.feature.news.ui.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadTopHeadlines()
    }

    fun selectCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadTopHeadlines(category)
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadTopHeadlines(_uiState.value.selectedCategory)
    }

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            newsRepository.toggleBookmark(articleId)
            // Optionally refresh the current category to reflect bookmark changes
            loadTopHeadlines(_uiState.value.selectedCategory)
        }
    }

    private fun loadTopHeadlines(category: Category? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(articlesState = NewsUiState.Loading) }

            when (val result = newsRepository.topHeadlines(category)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            articlesState = NewsUiState.Success(result.data),
                            isRefreshing = false
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            articlesState = NewsUiState.Error(result.throwable.message ?: "Unknown error"),
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }
}
