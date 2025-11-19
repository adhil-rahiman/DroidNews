package com.droidnotes.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val articleId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

