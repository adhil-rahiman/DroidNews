package com.droidnotes.feature.news.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.model.Article
import com.droidnotes.domain.news.repo.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BreakingNewsNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Check for breaking news (top headlines)
            when (val result = newsRepository.topHeadlines()) {
                is AppResult.Success -> {
                    val articles = result.data
                    if (articles.isNotEmpty()) {
                        // Show notification for the latest article
                        showBreakingNewsNotification(articles.first())
                        Result.success()
                    } else {
                        Result.success() // No news to show
                    }
                }
                is AppResult.Error -> {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showBreakingNewsNotification(article: Article) {

    }

    companion object {
        const val WORK_NAME = "breaking_news_notification_work"
        private const val CHANNEL_ID = "breaking_news_channel"
        private const val NOTIFICATION_ID = 1
    }
}