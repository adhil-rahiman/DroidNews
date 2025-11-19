package com.droidnotes.domain.news.usecase

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.repo.PagedNewsRepository

class ToggleBookmark(
    private val pagedNewsRepository: PagedNewsRepository
) {
    suspend operator fun invoke(id: String): AppResult<Unit> {
        return pagedNewsRepository.toggleBookmark(id)
    }
}