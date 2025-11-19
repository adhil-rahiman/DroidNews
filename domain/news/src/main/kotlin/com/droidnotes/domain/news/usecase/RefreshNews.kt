package com.droidnotes.domain.news.usecase

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import com.droidnotes.domain.news.repo.NewsRepository

class RefreshNews(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(
        category: Category? = null,
        clearCache: Boolean = false
    ): AppResult<List<Article>> {
        return newsRepository.refreshNews(category, clearCache)
    }

    suspend fun refreshAll(clearCache: Boolean = false): AppResult<Unit> {
        return newsRepository.refreshAllCategories(clearCache)
    }
}

