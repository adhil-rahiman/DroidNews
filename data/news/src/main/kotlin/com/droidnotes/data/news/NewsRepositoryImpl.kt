package com.droidnotes.data.news

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor() : NewsRepository {
    override suspend fun topHeadlines(category: Category?, page: Int): AppResult<List<Article>> =
        AppResult.Success(emptyList())

    override suspend fun search(query: String, page: Int): AppResult<List<Article>> =
        AppResult.Success(emptyList())

    override suspend fun getArticle(id: String): AppResult<Article> =
        AppResult.Error(UnsupportedOperationException("Not implemented"))

    override suspend fun toggleBookmark(id: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun bookmarks(): AppResult<List<Article>> =
        AppResult.Success(emptyList())
}
