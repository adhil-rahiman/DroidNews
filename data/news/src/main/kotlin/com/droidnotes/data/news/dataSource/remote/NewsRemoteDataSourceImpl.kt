package com.droidnotes.data.news.dataSource.remote

import com.droidnotes.common.AppResult
import com.droidnotes.core.network.GNewsApi
import com.droidnotes.core.network.mapper.toDomain
import com.droidnotes.core.network.util.ApiResponseHandler
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRemoteDataSourceImpl @Inject constructor(
    private val api: GNewsApi,
    private val ioDispatcher: CoroutineDispatcher
) : NewsRemoteDataSource {

    override suspend fun getTopHeadlines(
        category: Category?,
        page: Int
    ): AppResult<List<Article>> = withContext(ioDispatcher) {
        ApiResponseHandler.executeApiCall(
            apiCall = {
                val categoryParam = category?.name?.lowercase()
                api.getTopHeadlines(category = categoryParam, page = page)
            },
            transform = { response ->
                response.articles.map { it.toDomain() }
            }
        )
    }

    override suspend fun searchArticles(
        query: String,
        page: Int
    ): AppResult<List<Article>> = withContext(ioDispatcher) {
        ApiResponseHandler.executeApiCall(
            apiCall = {
                api.searchArticles(query = query, page = page)
            },
            transform = { response ->
                response.articles.map { it.toDomain() }
            }
        )
    }
}
