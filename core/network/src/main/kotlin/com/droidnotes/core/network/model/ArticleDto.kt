package com.droidnotes.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("content")
    val content: String? = null,
    @SerialName("url")
    val url: String,
    @SerialName("image")
    val image: String? = null,
    @SerialName("publishedAt")
    val publishedAt: String,
    @SerialName("source")
    val source: SourceDto
)
