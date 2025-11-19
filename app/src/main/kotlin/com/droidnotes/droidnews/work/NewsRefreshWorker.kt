package com.droidnotes.droidnews.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.NewsRepository
import com.droidnotes.domain.news.model.Category
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

@HiltWorker
class NewsRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = supervisorScope {
        try {
            // Refresh news for all categories concurrently
            val results = Category.entries.map { category ->
                async {
                    newsRepository.topHeadlines(category)
                }
            }.map { it.await() }

            // If any refresh succeeded, consider the work successful
            val hasSuccess = results.any { it is AppResult.Success }

            if (hasSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "news_refresh_work"
    }
}
