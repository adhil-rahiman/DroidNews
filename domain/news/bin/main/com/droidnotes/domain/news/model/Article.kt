package com.droidnotes.domain.news.model

import java.time.Instant

data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val source: Source,
    val publishedAt: Instant,
    val isBookmarked: Boolean = false,
)

data class Source(
    val id: String?,
    val name: String,
)

enum class Category { General, Business, Entertainment, Health, Science, Sports, Technology }
