package com.droidnotes.domain.news.usecase

import com.droidnotes.domain.news.repo.NewsRepository
import com.droidnotes.domain.news.model.Category

class GetTopHeadlines(private val repo: NewsRepository) {
    suspend operator fun invoke(category: Category? = null, page: Int = 1) =
        repo.topHeadlines(category, page)
}
