package com.droidnotes.domain.news.usecase

import com.droidnotes.domain.news.repo.NewsRepository

class SearchNews(private val repo: NewsRepository) {
    suspend operator fun invoke(query: String, page: Int = 1) =
        repo.search(query, page)
}
