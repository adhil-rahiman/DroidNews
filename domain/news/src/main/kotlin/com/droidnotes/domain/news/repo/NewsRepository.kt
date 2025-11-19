package com.droidnotes.domain.news.repo

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category

interface NewsRepository {
    suspend fun topHeadlines(category: Category? = null, page: Int = 1): AppResult<List<Article>>
    suspend fun getArticle(id: String): AppResult<Article>
    suspend fun refreshNews(category: Category? = null, clearCache: Boolean = false): AppResult<List<Article>>
    suspend fun refreshAllCategories(clearCache: Boolean = false): AppResult<Unit>

    suspend fun clearExpiredCache(cacheDurationMs: Long): AppResult<Unit>
}