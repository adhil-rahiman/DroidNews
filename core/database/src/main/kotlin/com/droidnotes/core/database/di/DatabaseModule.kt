package com.droidnotes.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.droidnotes.core.database.NewsDatabase
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.core.database.dao.BookmarkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create bookmarks table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS bookmarks (
                    articleId TEXT NOT NULL PRIMARY KEY,
                    bookmarkedAt INTEGER NOT NULL
                )
            """.trimIndent())
            
            // Migrate existing bookmarks from articles table
            database.execSQL("""
                INSERT OR IGNORE INTO bookmarks (articleId, bookmarkedAt)
                SELECT id, ${System.currentTimeMillis()}
                FROM articles
                WHERE isBookmarked = 1
            """.trimIndent())
            
            // Remove isBookmarked column from articles table
            // SQLite doesn't support DROP COLUMN, so we need to recreate the table
            database.execSQL("""
                CREATE TABLE articles_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    title TEXT NOT NULL,
                    description TEXT,
                    content TEXT,
                    url TEXT NOT NULL,
                    imageUrl TEXT,
                    sourceId TEXT,
                    sourceName TEXT NOT NULL,
                    publishedAt INTEGER NOT NULL,
                    category TEXT,
                    `query` TEXT,
                    page INTEGER NOT NULL,
                    timestamp INTEGER NOT NULL
                )
            """.trimIndent())
            
            database.execSQL("""
                INSERT INTO articles_new
                SELECT id, title, description, content, url, imageUrl, sourceId, sourceName,
                       publishedAt, category, `query`, page, timestamp
                FROM articles
            """.trimIndent())
            
            database.execSQL("DROP TABLE articles")
            database.execSQL("ALTER TABLE articles_new RENAME TO articles")
        }
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(
        @ApplicationContext context: Context
    ): NewsDatabase {
        return Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news_database"
        )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: NewsDatabase): ArticleDao {
        return database.articleDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: NewsDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}
