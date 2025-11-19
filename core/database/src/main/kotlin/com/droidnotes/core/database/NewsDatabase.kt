package com.droidnotes.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.core.database.model.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
