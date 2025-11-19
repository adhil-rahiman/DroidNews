package com.droidnotes.domain.news.usecase

import com.droidnotes.domain.news.repo.NewsRepository

class GetArticle(private val repo: NewsRepository) {
    suspend operator fun invoke(id: String) = repo.getArticle(id)
}
