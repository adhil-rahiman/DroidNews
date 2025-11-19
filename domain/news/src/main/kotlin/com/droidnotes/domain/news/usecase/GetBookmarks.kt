package com.droidnotes.domain.news.usecase

import com.droidnotes.domain.news.repo.NewsRepository

class GetBookmarks(private val repo: NewsRepository) {
    suspend operator fun invoke() = repo.bookmarks()
}
