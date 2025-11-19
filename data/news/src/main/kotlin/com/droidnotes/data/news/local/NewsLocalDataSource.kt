package com.droidnotes.data.news.local

import com.droidnotes.common.AppResult
import com.droidnotes.core.database.dao.ArticleDao
import com.droidnotes.data.news.mapper.toDomain
import com.droidnotes.data.news.mapper.toEntity
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface NewsLocalDataSource {
    suspend fun insertArticles(articles: List<Article>, category: Category? = null, query: String? = null, page: Int = 1)
    suspend fun getArticlesByCategory(category: Category?, page: Int = 1, pageSize: Int = 5): AppResult<List<Article>>
    suspend fun getArticlesByQuery(query: String, page: Int = 1, pageSize: Int = 5): AppResult<List<Article>>
    suspend fun getArticleById(id: String): AppResult<Article?>
    fun getBookmarkedArticles(): Flow<List<Article>>
    suspend fun toggleBookmark(id: String): AppResult<Unit>
    suspend fun clearExpiredCache(cacheDurationMs: Long = 24 * 60 * 60 * 1000) // 24 hours
}

class NewsLocalDataSourceImpl @Inject constructor(
    private val articleDao: ArticleDao,
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
            val article = entity?.toDomain()
            AppResult.Success(article)
        } catch (throwable: Throwable) {
            AppResult.Error(throwable)
        }
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return articleDao.getBookmarkedArticles()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun toggleBookmark(id: String): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            val currentEntity = articleDao.getArticleById(id)
            if (currentEntity != null) {
                val updatedEntity = currentEntity.copy(isBookmarked = !currentEntity.isBookmarked)
                articleDao.updateArticle(updatedEntity)
                AppResult.Success(Unit)
            } else {
                AppResult.Error(Exception("Article not found"))
            }
        } catch (throwable: Throwable) {
            AppResult.Error(throwable)
        }
    }

    override suspend fun clearExpiredCache(cacheDurationMs: Long) = withContext(ioDispatcher) {
        val expiryTimestamp = System.currentTimeMillis() - cacheDurationMs
        articleDao.deleteExpiredArticles(expiryTimestamp)
    }
}
