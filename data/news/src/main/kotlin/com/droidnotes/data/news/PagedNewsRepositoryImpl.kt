package com.droidnotes.data.news

import androidx.paging.PagingData
import com.droidnotes.data.news.paging.NewsPagingDataSource
import com.droidnotes.domain.news.PagedNewsRepository
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagedNewsRepositoryImpl @Inject constructor(
    private val pagingDataSource: NewsPagingDataSource
) : PagedNewsRepository {

    override fun topHeadlines(category: Category?): Flow<PagingData<Article>> {
        return pagingDataSource.getTopHeadlinesPager(category)
    }

    override fun search(query: String): Flow<PagingData<Article>> {
        return pagingDataSource.getSearchPager(query)
    }

    override fun bookmarks(): Flow<PagingData<Article>> {
        return pagingDataSource.getBookmarkedArticles()
    }
}
