package com.droidnotes.data.news.dataSource.paging

import androidx.paging.PagingData
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.flow.Flow

interface NewsPagingDataSource {
    fun getTopHeadlinesPager(category: Category? = null): Flow<PagingData<Article>>
    fun getSearchPager(query: String): Flow<PagingData<Article>>
    fun getBookmarkedArticles(): Flow<PagingData<Article>>
}