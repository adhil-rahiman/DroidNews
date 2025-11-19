package com.droidnotes.data.news.dataSource.remote

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category

interface NewsRemoteDataSource {
    suspend fun getTopHeadlines(
        category: Category? = null,
        page: Int = 1
    ): AppResult<List<Article>>

    suspend fun searchArticles(
        query: String,
        page: Int = 1
    ): AppResult<List<Article>>
}