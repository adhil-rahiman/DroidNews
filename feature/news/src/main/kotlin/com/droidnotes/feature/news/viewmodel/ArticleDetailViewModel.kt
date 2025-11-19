package com.droidnotes.feature.news.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.feature.news.ui.ArticleDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId: String = checkNotNull(savedStateHandle["articleId"])

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        loadArticle()
    }

    fun toggleBookmark() {
        val currentState = _uiState.value
        if (currentState is ArticleDetailUiState.Success) {
            viewModelScope.launch {
                when (newsRepository.toggleBookmark(articleId)) {
                    is AppResult.Success -> {
                        // Reload article to get updated bookmark status
                        loadArticle()
                    }
                    is AppResult.Error -> {
                        // Could show a snackbar or toast here
                    }
                }
            }
        }
    }

    private fun loadArticle() {
        viewModelScope.launch {
            _uiState.update { ArticleDetailUiState.Loading }

            when (val result = newsRepository.getArticle(articleId)) {
                is AppResult.Success -> {
                    _uiState.update { ArticleDetailUiState.Success(result.data) }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        ArticleDetailUiState.Error(result.throwable.message ?: "Failed to load article")
                    }
                }
            }
        }
    }
}
