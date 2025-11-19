package com.droidnotes.domain.news.usecase

import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.repo.NewsRepository
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.model.Category
import com.droidnotes.domain.news.model.Source
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Instant

class GetTopHeadlinesTest {

    private val mockRepository: NewsRepository = mock()
    private val useCase = GetTopHeadlines(mockRepository)

    @Test
    fun `invoke should return success result from repository`() = runTest {
        // Given
        val category = Category.GENERAL
        val expectedArticles = listOf(
            createTestArticle("1"),
            createTestArticle("2")
        )
        whenever(mockRepository.topHeadlines(category)).thenReturn(AppResult.Success(expectedArticles))

        // When
        val result = useCase(category)

        // Then
        assertTrue(result is AppResult.Success)
        assertEquals(expectedArticles, (result as AppResult.Success).data)
    }

    @Test
    fun `invoke should return error result from repository`() = runTest {
        // Given
        val category = Category.BUSINESS
        val expectedError = RuntimeException("Network error")
        whenever(mockRepository.topHeadlines(category)).thenReturn(AppResult.Error(expectedError))

        // When
        val result = useCase(category)

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(expectedError, (result as AppResult.Error).throwable)
    }

    @Test
    fun `invoke should handle null category`() = runTest {
        // Given
        val expectedArticles = listOf(createTestArticle("1"))
        whenever(mockRepository.topHeadlines(null)).thenReturn(AppResult.Success(expectedArticles))

        // When
        val result = useCase(null)

        // Then
        assertTrue(result is AppResult.Success)
        assertEquals(expectedArticles, (result as AppResult.Success).data)
    }

    private fun createTestArticle(id: String): Article {
        return Article(
            id = id,
            title = "Test Article $id",
            description = "Test description $id",
            content = "Test content $id",
            url = "https://example.com/article/$id",
            imageUrl = "https://example.com/image/$id.jpg",
            source = Source(id = "test-source", name = "Test Source"),
            publishedAt = Instant.now(),
            isBookmarked = false
        )
    }
}
