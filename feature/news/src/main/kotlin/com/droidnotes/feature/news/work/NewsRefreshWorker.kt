package com.droidnotes.feature.news.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.repo.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.TimeUnit

@HiltWorker
class NewsRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsRepository: NewsRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = supervisorScope {
        try {
            val cacheExpiryMinutes = inputData.getLong(
                KEY_CACHE_EXPIRY_MINUTES,
                WorkConfig.CACHE_EXPIRY_MINUTES
            )

            newsRepository.clearExpiredCache(
                cacheDurationMs = TimeUnit.MINUTES.toMillis(cacheExpiryMinutes)
            )

            when (val result = newsRepository.refreshAllCategories(clearCache = false)) {
                is AppResult.Success -> Result.success()
                is AppResult.Error -> Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "news_refresh_work"
        const val KEY_CACHE_EXPIRY_MINUTES = "cache_expiry_minutes"
    }
}