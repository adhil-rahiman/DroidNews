package com.droidnotes.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.droidnotes.core.database.model.ArticleEntity
import com.droidnotes.core.database.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE articleId = :articleId")
    suspend fun deleteBookmark(articleId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE articleId = :articleId)")
    suspend fun isBookmarked(articleId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE articleId = :articleId)")
    fun isBookmarkedFlow(articleId: String): Flow<Boolean>

    @Transaction
    @Query("""
        SELECT a.* FROM articles a
        INNER JOIN bookmarks b ON a.id = b.articleId
        ORDER BY b.bookmarkedAt DESC
    """)
    fun getBookmarkedArticlesPaging(): PagingSource<Int, ArticleEntity>

    @Transaction
    @Query("""
        SELECT a.* FROM articles a
        INNER JOIN bookmarks b ON a.id = b.articleId
        ORDER BY b.bookmarkedAt DESC
    """)
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int
}

