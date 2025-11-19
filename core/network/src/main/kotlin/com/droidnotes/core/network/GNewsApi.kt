package com.droidnotes.core.network

import com.droidnotes.core.network.model.ArticleDto
import com.droidnotes.core.network.model.SearchResponse
import com.droidnotes.core.network.model.TopHeadlinesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GNewsApi {

    @GET("v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 5,
        @Query("lang") lang: String = "en"
    ): Response<TopHeadlinesResponse>

    @GET("v4/search")
    suspend fun searchArticles(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 5,
        @Query("lang") lang: String = "en",
        @Query("sortby") sortBy: String = "publishedAt"
    ): Response<SearchResponse>
}
