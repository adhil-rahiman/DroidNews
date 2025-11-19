package com.droidnotes.data.news

import com.droidnotes.common.AppResult
import com.droidnotes.data.news.local.NewsLocalDataSource
import com.droidnotes.data.news.remote.NewsRemoteDataSource
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun search(query: String, page: Int): AppResult<List<Article>> {
        // Try to get from cache first
        val cachedResult = localDataSource.getArticlesByQuery(query, page)
        if (cachedResult is AppResult.Success && cachedResult.data.isNotEmpty()) {
            return cachedResult
        }

        // Fetch from remote and cache
        return when (val remoteResult = remoteDataSource.searchArticles(query, page)) {
            is AppResult.Success -> {
                localDataSource.insertArticles(remoteResult.data, query = query, page = page)
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

    override suspend fun toggleBookmark(id: String): AppResult<Unit> =
        localDataSource.toggleBookmark(id)

    override suspend fun bookmarks(): AppResult<List<Article>> {
        return try {
            val articles = localDataSource.getBookmarkedArticles().first()
            AppResult.Success(articles)
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    suspend fun getBookmarkedArticlesFlow(): Flow<List<Article>> =
        localDataSource.getBookmarkedArticles()
}
