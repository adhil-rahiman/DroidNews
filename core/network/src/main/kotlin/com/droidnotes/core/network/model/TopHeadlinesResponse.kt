package com.droidnotes.core.network.model

import android.R.attr.data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopHeadlinesResponse(
    @SerialName("totalArticles")
    val totalArticles: Int,
    @SerialName("articles")
    val articles: List<ArticleDto>
)
