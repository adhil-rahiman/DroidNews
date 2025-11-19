package com.droidnotes.domain.news.repo

import androidx.paging.PagingData
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.flow.Flow

interface PagedNewsRepository {
    fun topHeadlines(category: Category? = null): Flow<PagingData<Article>>
    fun search(query: String): Flow<PagingData<Article>>
    fun bookmarks(): Flow<PagingData<Article>>
    suspend fun toggleBookmark(id: String): AppResult<Unit>
}