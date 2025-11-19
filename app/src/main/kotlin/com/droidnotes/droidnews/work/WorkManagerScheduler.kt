package com.droidnotes.droidnews.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
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
    private val workManager = WorkManager.getInstance(context)

    fun scheduleNewsRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshWork = PeriodicWorkRequestBuilder<NewsRefreshWorker>(
            1, // Repeat interval
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            NewsRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            refreshWork
        )
    }

    fun scheduleBreakingNewsNotification() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<BreakingNewsNotificationWorker>(
            2, // Repeat interval
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            BreakingNewsNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )
    }

    fun triggerImmediateNewsRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshWork = OneTimeWorkRequestBuilder<NewsRefreshWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(refreshWork)
    }

    fun cancelAllWork() {
        workManager.cancelUniqueWork(NewsRefreshWorker.WORK_NAME)
        workManager.cancelUniqueWork(BreakingNewsNotificationWorker.WORK_NAME)
    }
}
