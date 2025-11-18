package com.droidnotes.domain.news

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category

interface NewsRepository {
    suspend fun topHeadlines(category: Category? = null, page: Int = 1): AppResult<List<Article>>
    suspend fun search(query: String, page: Int = 1): AppResult<List<Article>>
    suspend fun getArticle(id: String): AppResult<Article>
    suspend fun toggleBookmark(id: String): AppResult<Unit>
    suspend fun bookmarks(): AppResult<List<Article>>
}
