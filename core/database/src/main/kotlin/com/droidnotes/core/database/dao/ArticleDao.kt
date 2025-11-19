package com.droidnotes.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.droidnotes.core.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesByCategory(category: String?, limit: Int, offset: Int): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE query = :query ORDER BY publishedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getArticlesByQuery(query: String, limit: Int, offset: Int): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    suspend fun getBookmarkedArticlesSnapshot(): List<ArticleEntity>

    @Query("UPDATE articles SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)

    @Query("DELETE FROM articles WHERE timestamp < :timestamp")
    suspend fun deleteExpiredArticles(timestamp: Long)

    @Query("SELECT COUNT(*) FROM articles WHERE category = :category")
    suspend fun getArticleCountByCategory(category: String?): Int

    @Query("SELECT COUNT(*) FROM articles WHERE query = :query")
    suspend fun getArticleCountByQuery(query: String): Int
}
