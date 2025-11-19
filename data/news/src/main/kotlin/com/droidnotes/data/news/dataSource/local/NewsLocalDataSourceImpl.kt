package com.droidnotes.data.news.dataSource.local

import com.droidnotes.common.AppResult
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.core.database.dao.BookmarkDao
import com.droidnotes.core.database.model.BookmarkEntity
import com.droidnotes.data.news.mapper.toDomain
import com.droidnotes.data.news.mapper.toEntity
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsLocalDataSourceImpl @Inject constructor(
    private val articleDao: ArticleDao,
    private val bookmarkDao: BookmarkDao,
    private val ioDispatcher: CoroutineDispatcher
) : NewsLocalDataSource {

    override suspend fun insertArticles(
        articles: List<Article>,
        category: Category?,
        query: String?,
        page: Int
    ) = withContext(ioDispatcher) {
        val entities = articles.map { it.toEntity(category?.name, query, page) }
        articleDao.insertArticles(entities)
    }

    override suspend fun getArticlesByCategory(
        category: Category?,
        page: Int,
        pageSize: Int
    ): AppResult<List<Article>> = withContext(ioDispatcher) {
        try {
            val offset = (page - 1) * pageSize
            val entities = articleDao.getArticlesByCategory(category?.name, pageSize, offset)
            val articles = entities.map { it.toDomain() }
            AppResult.Success(articles)
        } catch (throwable: Throwable) {
            AppResult.Error(throwable)
        }
    }

    override suspend fun getArticlesByQuery(
        query: String,
        page: Int,
        pageSize: Int
    ): AppResult<List<Article>> = withContext(ioDispatcher) {
        try {
            val offset = (page - 1) * pageSize
            val entities = articleDao.getArticlesByQuery(query, pageSize, offset)
            val articles = entities.map { it.toDomain() }
            AppResult.Success(articles)
        } catch (throwable: Throwable) {
            AppResult.Error(throwable)
        }
    }

    override suspend fun getArticleById(id: String): AppResult<Article?> = withContext(ioDispatcher) {
        try {
            val entity = articleDao.getArticleById(id)
            val isBookmarked = entity?.let { bookmarkDao.isBookmarked(it.id) } ?: false
            val article = entity?.toDomain(isBookmarked)
            AppResult.Success(article)
        } catch (throwable: Throwable) {
            AppResult.Error(throwable)
        }
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return bookmarkDao.getBookmarkedArticles()
            .map { entities -> entities.map { it.toDomain(isBookmarked = true) } }
    }

    override suspend fun toggleBookmark(article: Article): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            val isCurrentlyBookmarked = bookmarkDao.isBookmarked(article.id)
            
            if (isCurrentlyBookmarked) {
                bookmarkDao.deleteBookmark(article.id)
            } else {
                val existingArticle = articleDao.getArticleById(article.id)
                if (existingArticle == null) {
                    articleDao.insertArticle(article.toEntity())
                }
                bookmarkDao.insertBookmark(BookmarkEntity(article.id))
            }
            
            AppResult.Success(Unit)
        } catch (throwable: Throwable) {
            AppResult.Error(throwable)
        }
    }

    override suspend fun clearExpiredCache(cacheDurationMs: Long) = withContext(ioDispatcher) {
        val expiryTimestamp = System.currentTimeMillis() - cacheDurationMs
        articleDao.deleteExpiredArticles(expiryTimestamp)
    }
}