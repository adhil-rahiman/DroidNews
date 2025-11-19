package com.droidnotes.data.news.dataSource.local

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.flow.Flow

interface NewsLocalDataSource {
    suspend fun insertArticles(articles: List<Article>, category: Category? = null, query: String? = null, page: Int = 1)
    suspend fun getArticlesByCategory(category: Category?, page: Int = 1, pageSize: Int = 5): AppResult<List<Article>>
    suspend fun getArticlesByQuery(query: String, page: Int = 1, pageSize: Int = 5): AppResult<List<Article>>
    suspend fun getArticleById(id: String): AppResult<Article?>
    fun getBookmarkedArticles(): Flow<List<Article>>
    suspend fun toggleBookmark(article: Article): AppResult<Unit>
    suspend fun clearExpiredCache(cacheDurationMs: Long = 24 * 60 * 60 * 1000) // 24 hours
}
