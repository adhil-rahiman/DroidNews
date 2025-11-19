package com.droidnotes.data.news.repo

import androidx.paging.PagingData
import com.droidnotes.common.AppResult
import com.droidnotes.data.news.dataSource.local.NewsLocalDataSource
import com.droidnotes.data.news.dataSource.paging.NewsPagingDataSource
import com.droidnotes.domain.news.repo.PagedNewsRepository
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagedNewsRepositoryImpl @Inject constructor(
    private val pagingDataSource: NewsPagingDataSource,
    private val localDataSource: NewsLocalDataSource
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

    override suspend fun toggleBookmark(id: String): AppResult<Unit> {
        return localDataSource.toggleBookmark(id)
    }
}