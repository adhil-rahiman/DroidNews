package com.droidnotes.data.news.repo

import com.droidnotes.common.AppResult
import com.droidnotes.data.news.dataSource.local.NewsLocalDataSource
import com.droidnotes.data.news.dataSource.remote.NewsRemoteDataSource
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import com.droidnotes.domain.news.repo.NewsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource,
    private val localDataSource: NewsLocalDataSource
) : NewsRepository {

    override suspend fun topHeadlines(category: Category?, page: Int): AppResult<List<Article>> {
        // Try to get from cache first
        val cachedResult = localDataSource.getArticlesByCategory(category, page)
        if (cachedResult is AppResult.Success && cachedResult.data.isNotEmpty()) {
            return cachedResult
        }

        // Fetch from remote and cache
        return when (val remoteResult = remoteDataSource.getTopHeadlines(category, page)) {
            is AppResult.Success -> {
                localDataSource.insertArticles(remoteResult.data, category, page = page)
                remoteResult
            }
            is AppResult.Error -> {
                // Return cached data if available, otherwise return error
                if (cachedResult is AppResult.Success) {
                    cachedResult
                } else {
                    remoteResult
                }
            }
        }
    }


    override suspend fun getArticle(id: String): AppResult<Article> {
        return when (val localResult = localDataSource.getArticleById(id)) {
            is AppResult.Success -> {
                if (localResult.data != null) {
                    AppResult.Success(localResult.data!!)
                } else {
                    AppResult.Error(Exception("Article not found"))
                }
            }
            is AppResult.Error -> localResult
        }
    }

    override suspend fun refreshNews(category: Category?, clearCache: Boolean): AppResult<List<Article>> {
        return try {
            if (clearCache) {
                localDataSource.clearExpiredCache(cacheDurationMs = 0)
            }

            when (val remoteResult = remoteDataSource.getTopHeadlines(category, page = 1)) {
                is AppResult.Success -> {
                    localDataSource.insertArticles(remoteResult.data, category, page = 1)
                    remoteResult
                }
                is AppResult.Error -> remoteResult
            }
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun refreshAllCategories(clearCache: Boolean): AppResult<Unit> {
        return try {
            if (clearCache) {
                localDataSource.clearExpiredCache(cacheDurationMs = 0)
            }

            coroutineScope {
                val allCategories = listOf(null) + Category.entries
                
                val results = allCategories.map { category ->
                    async {
                        remoteDataSource.getTopHeadlines(category, page = 1)
                    }
                }.awaitAll()

                results.forEach { result ->
                    if (result is AppResult.Success) {
                        val category = result.data.firstOrNull()?.let { 
                            Category.entries.find { cat -> 
                                result.data.any { article -> article.toString().contains(cat.name, ignoreCase = true) }
                            }
                        }
                        localDataSource.insertArticles(result.data, category, page = 1)
                    }
                }

                val hasAnySuccess = results.any { it is AppResult.Success }
                if (hasAnySuccess) {
                    AppResult.Success(Unit)
                } else {
                    val firstError = results.firstOrNull { it is AppResult.Error } as? AppResult.Error
                    firstError ?: AppResult.Error(Exception("Failed to refresh any category"))
                }
            }
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun clearExpiredCache(cacheDurationMs: Long): AppResult<Unit> {
        localDataSource.clearExpiredCache(cacheDurationMs)
        return AppResult.Success(Unit)
    }

}