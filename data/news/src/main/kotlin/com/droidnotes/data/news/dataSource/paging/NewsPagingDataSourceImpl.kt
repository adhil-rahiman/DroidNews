package com.droidnotes.data.news.dataSource.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.core.database.dao.BookmarkDao
import com.droidnotes.data.news.mapper.toDomain
import com.droidnotes.data.news.dataSource.remote.NewsRemoteDataSource
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsPagingDataSourceImpl @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource,
    private val articleDao: ArticleDao,
    private val bookmarkDao: BookmarkDao,
    private val ioDispatcher: CoroutineDispatcher
): NewsPagingDataSource {

    override fun getTopHeadlinesPager(category: Category?): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE * 2,
                prefetchDistance = PAGE_SIZE / 2
            ),
            remoteMediator = NewsRemoteMediator(
                remoteDataSource = remoteDataSource,
                articleDao = articleDao,
                ioDispatcher = ioDispatcher,
                category = category
            ),
            pagingSourceFactory = {
                when (category) {
                    null -> articleDao.getAllPagedArticles()
                    else -> articleDao.getPagedArticlesByCategory(category.name)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val isBookmarked = runBlocking { bookmarkDao.isBookmarked(entity.id) }
                entity.toDomain(isBookmarked)
            }
        }
    }

    override fun getSearchPager(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE * 2,
                prefetchDistance = PAGE_SIZE / 2
            ),
            remoteMediator = NewsRemoteMediator(
                remoteDataSource = remoteDataSource,
                articleDao = articleDao,
                ioDispatcher = ioDispatcher,
                query = query
            ),
            pagingSourceFactory = {
                articleDao.getPagedArticlesByQuery(query)
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val isBookmarked = runBlocking { bookmarkDao.isBookmarked(entity.id) }
                entity.toDomain(isBookmarked)
            }
        }
    }

    override fun getBookmarkedArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                bookmarkDao.getBookmarkedArticlesPaging()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomain(isBookmarked = true)
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}