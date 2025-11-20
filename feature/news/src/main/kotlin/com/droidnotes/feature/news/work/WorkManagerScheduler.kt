package com.droidnotes.feature.news.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.Companion.getInstance(context)

    fun scheduleNewsRefresh(
        refreshIntervalMinutes: Long = WorkConfig.NEWS_REFRESH_INTERVAL_MINUTES,
        cacheExpiryMinutes: Long = WorkConfig.CACHE_EXPIRY_MINUTES
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putLong(NewsRefreshWorker.KEY_CACHE_EXPIRY_MINUTES, cacheExpiryMinutes)
            .build()

        val refreshWork = PeriodicWorkRequestBuilder<NewsRefreshWorker>(
            refreshIntervalMinutes,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            NewsRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            refreshWork
        )
    }

    fun scheduleBreakingNewsNotification(
        intervalHours: Long = WorkConfig.BREAKING_NEWS_INTERVAL_HOURS
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<BreakingNewsNotificationWorker>(
            intervalHours,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            BreakingNewsNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )
    }

    fun cancelAllWork() {
        workManager.cancelUniqueWork(NewsRefreshWorker.WORK_NAME)
        workManager.cancelUniqueWork(BreakingNewsNotificationWorker.WORK_NAME)
    }
}