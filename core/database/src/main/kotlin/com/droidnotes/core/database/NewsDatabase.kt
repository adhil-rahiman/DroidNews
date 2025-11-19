package com.droidnotes.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.core.database.dao.BookmarkDao
import com.droidnotes.core.database.model.ArticleEntity
import com.droidnotes.core.database.model.BookmarkEntity

@Database(
    entities = [ArticleEntity::class, BookmarkEntity::class],
    version = 2,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun bookmarkDao(): BookmarkDao
}
