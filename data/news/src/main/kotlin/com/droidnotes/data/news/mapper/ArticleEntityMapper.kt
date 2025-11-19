package com.droidnotes.data.news.mapper

import com.droidnotes.core.database.model.ArticleEntity
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Source
import java.time.Instant

fun Article.toEntity(
    category: String? = null,
    query: String? = null,
    page: Int = 1
): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        sourceId = source.id,
        sourceName = source.name,
        publishedAt = publishedAt.toEpochMilli(),
        category = category,
        query = query,
        page = page,
        timestamp = System.currentTimeMillis()
    )
}

fun ArticleEntity.toDomain(isBookmarked: Boolean = false): Article {
    return Article(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        source = Source(
            id = sourceId,
            name = sourceName
        ),
        publishedAt = Instant.ofEpochMilli(publishedAt),
        isBookmarked = isBookmarked
    )
}
