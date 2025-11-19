package com.droidnotes.data.news.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.data.news.remote.NewsRemoteDataSource
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.droidnotes.data.news.mapper.toDomain as toDomainArticle

@OptIn(ExperimentalPagingApi::class)
class NewsPagingDataSource @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource,
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun getTopHeadlinesPager(category: Category? = null): Flow<PagingData<Article>> {
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
                entity.toDomainArticle()
            }
        }
    }

    fun getSearchPager(query: String): Flow<PagingData<Article>> {
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
                entity.toDomainArticle()
            }
        }
    }

    fun getBookmarkedArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                articleDao.getBookmarkedArticlesPaging()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomainArticle()
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
