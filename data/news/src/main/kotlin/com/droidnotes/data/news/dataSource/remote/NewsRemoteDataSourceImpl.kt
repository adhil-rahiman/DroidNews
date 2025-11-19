package com.droidnotes.data.news.dataSource.remote

import com.droidnotes.common.AppResult
import com.droidnotes.core.network.GNewsApi
import com.droidnotes.core.network.mapper.toDomain
import com.droidnotes.core.network.model.SearchResponse
import com.droidnotes.core.network.model.TopHeadlinesResponse
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NewsRemoteDataSourceImpl @Inject constructor(
    private val api: GNewsApi,
    private val ioDispatcher: CoroutineDispatcher
) : NewsRemoteDataSource {

    override suspend fun getTopHeadlines(
        category: Category?,
        page: Int
    ): AppResult<List<Article>> = withContext(ioDispatcher) {
        runCatching {
            val categoryParam = category?.name?.lowercase()
            val response = api.getTopHeadlines(
                category = categoryParam,
                page = page
            )
            handleApiResponse(response)
        }.getOrElse { throwable ->
            AppResult.Error(throwable)
        }
    }

    override suspend fun searchArticles(
        query: String,
        page: Int
    ): AppResult<List<Article>> = withContext(ioDispatcher) {
        runCatching {
            val response = api.searchArticles(
                query = query,
                page = page
            )
            handleApiResponse(response)
        }.getOrElse { throwable ->
            AppResult.Error(throwable)
        }
    }

    private fun <T> handleApiResponse(response: Response<T>): AppResult<List<Article>> {
        return if (response.isSuccessful) {
            when (val body = response.body()) {
                is TopHeadlinesResponse -> {
                    val articles = body.articles.map { it.toDomain() }
                    AppResult.Success(articles)
                }
                is SearchResponse -> {
                    val articles = body.articles.map { it.toDomain() }
                    AppResult.Success(articles)
                }
                else -> {
                    AppResult.Error(IllegalStateException("Unexpected response type"))
                }
            }
        } else {
            AppResult.Error(Exception("API Error: ${response.code()} ${response.message()}"))
        }
    }
}
