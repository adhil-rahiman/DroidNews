package com.droidnotes.feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.feature.news.ui.BookmarksUiState
import com.droidnotes.feature.news.ui.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    fun refresh() {
        loadBookmarks()
    }

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            newsRepository.toggleBookmark(articleId)
            // Refresh bookmarks after toggle
            loadBookmarks()
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.update { it.copy(articlesState = NewsUiState.Loading) }

            when (val result = newsRepository.bookmarks()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(articlesState = NewsUiState.Success(result.data))
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            articlesState = NewsUiState.Error(
                                result.throwable.message ?: "Failed to load bookmarks"
                            )
                        )
                    }
                }
            }
        }
    }
}
