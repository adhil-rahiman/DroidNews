package com.droidnotes.droidnews.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.droidnotes.common.AppResult
import com.droidnotes.domain.news.repo.NewsRepository
import com.droidnotes.droidnews.MainActivity
import com.droidnotes.droidnews.R
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

    private fun showBreakingNewsNotification(article: com.droidnotes.domain.news.model.Article) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Breaking News",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Breaking news notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open the app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_article", article.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You'll need to create this
            .setContentTitle("Breaking News")
            .setContentText(article.title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${article.title}\n\n${article.description ?: ""}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val WORK_NAME = "breaking_news_notification_work"
        private const val CHANNEL_ID = "breaking_news_channel"
        private const val NOTIFICATION_ID = 1
    }
}
