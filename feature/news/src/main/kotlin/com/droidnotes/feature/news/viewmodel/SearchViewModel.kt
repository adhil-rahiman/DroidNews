package com.droidnotes.feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.feature.news.ui.NewsUiState
import com.droidnotes.feature.news.ui.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        observeSearchQuery()
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(articlesState = NewsUiState.Success(emptyList())) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, articlesState = NewsUiState.Loading) }

            when (val result = newsRepository.search(query)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            articlesState = NewsUiState.Success(result.data),
                            isSearching = false
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            articlesState = NewsUiState.Error(result.throwable.message ?: "Search failed"),
                            isSearching = false
                        )
                    }
                }
            }
        }
    }

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            newsRepository.toggleBookmark(articleId)
            // Refresh current search results to reflect bookmark changes
            val currentQuery = _uiState.value.query
            if (currentQuery.isNotBlank()) {
                search(currentQuery)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _uiState
            .map { it.query }
            .distinctUntilChanged()
            .debounce(500) // Wait 500ms after user stops typing
            .filter { it.isNotBlank() }
            .onEach { query -> search(query) }
            .launchIn(viewModelScope)
    }
}
