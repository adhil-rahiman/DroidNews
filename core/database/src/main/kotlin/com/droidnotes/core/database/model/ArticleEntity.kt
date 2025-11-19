package com.droidnotes.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val sourceId: String?,
    val sourceName: String,
    val publishedAt: Long, // Store as timestamp
    val isBookmarked: Boolean = false,
    val category: String? = null, // For caching categorized articles
    val query: String? = null, // For caching search results
    val page: Int = 1, // For pagination
    val timestamp: Long = System.currentTimeMillis() // For cache expiry
)
