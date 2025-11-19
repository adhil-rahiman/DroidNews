package com.droidnotes.data.news.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.droidnotes.common.AppResult
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.core.database.model.ArticleEntity
import com.droidnotes.data.news.mapper.toEntity
import com.droidnotes.data.news.remote.NewsRemoteDataSource
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource,
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher,
    private val category: Category? = null,
    private val query: String? = null
) : RemoteMediator<Int, ArticleEntity>() {

    private var lastRequestTime = 0L

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult = withContext(ioDispatcher) {
        try {
            // Rate limiting: ensure at least 1 second between API calls
            val currentTime = System.currentTimeMillis()
            val timeSinceLastRequest = currentTime - lastRequestTime
            if (timeSinceLastRequest < MIN_REQUEST_INTERVAL_MS) {
                kotlinx.coroutines.delay(MIN_REQUEST_INTERVAL_MS - timeSinceLastRequest)
            }
            lastRequestTime = System.currentTimeMillis()

            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return@withContext MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.page + 1).coerceAtMost(MAX_PAGE)
                    }
                }
            }

            val articles = when {
                query != null -> {
                    // Search articles
                    when (val result = remoteDataSource.searchArticles(query, page)) {
                        is AppResult.Success -> result.data
                        is AppResult.Error -> return@withContext MediatorResult.Error(result.throwable)
                    }
                }
                else -> {
                    // Get top headlines by category
                    when (val result = remoteDataSource.getTopHeadlines(category, page)) {
                        is AppResult.Success -> {
                            Log.d("TestLogs", "Success result: ${result.data}")
                            result.data
                        }
                        is AppResult.Error -> {
                            Log.d("TestLogs", "Error result: ${result.throwable}")
                            return@withContext MediatorResult.Error(result.throwable)
                            }
                    }
                }
            }

            if (loadType == LoadType.REFRESH) {
                // Clear existing data for refresh
                when {
                    query != null -> articleDao.deleteExpiredArticles(System.currentTimeMillis() - CACHE_DURATION_MS)
                    category != null -> articleDao.deleteExpiredArticles(System.currentTimeMillis() - CACHE_DURATION_MS)
                    else -> articleDao.deleteExpiredArticles(System.currentTimeMillis() - CACHE_DURATION_MS)
                }
            }

            // Insert new articles
            val entities = articles.map { it.toEntity(category?.name, query, page) }
            articleDao.insertArticles(entities)

            MediatorResult.Success(
                endOfPaginationReached = articles.size < state.config.pageSize || page >= MAX_PAGE
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val MAX_PAGE = 10 // Limit pagination to prevent infinite scrolling
        private const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L // 24 hours
        private const val MIN_REQUEST_INTERVAL_MS = 1000L // 1 second minimum between API calls
    }
}
