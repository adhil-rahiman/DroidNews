package com.droidnotes.core.network.mapper

import com.droidnotes.core.network.model.ArticleDto
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Source
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

private val dateFormatter = DateTimeFormatter.ISO_INSTANT

fun ArticleDto.toDomain(): Article {
    return Article(
        id = url.hashCode().toString(), // Use URL hash as unique ID
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = image,
        source = source.toDomain(),
        publishedAt = parsePublishedAt(publishedAt),
        isBookmarked = false // This will be determined by local storage
    )
}

fun com.droidnotes.core.network.model.SourceDto.toDomain(): Source {
    return Source(
        id = id,
        name = name
    )
}

private fun parsePublishedAt(publishedAt: String): Instant {
    return try {
        Instant.parse(publishedAt)
    } catch (e: Exception) {
        // Fallback to current time if parsing fails
        Instant.now()
    }
}
