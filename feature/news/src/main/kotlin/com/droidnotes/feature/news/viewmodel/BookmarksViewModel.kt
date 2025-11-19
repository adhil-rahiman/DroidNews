package com.droidnotes.feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.domain.news.PagedNewsRepository
import com.droidnotes.domain.news.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val pagedNewsRepository: PagedNewsRepository
) : ViewModel() {

    val articlesPagingFlow: Flow<PagingData<Article>> = pagedNewsRepository
        .bookmarks()
        .cachedIn(viewModelScope)

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            newsRepository.toggleBookmark(articleId)
        }
    }
}
