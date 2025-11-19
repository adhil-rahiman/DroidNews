package com.droidnotes.droidnews

import android.app.Application
import com.droidnotes.droidnews.work.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DroidNewsApp : Application() {

    @Inject
    lateinit var workManagerScheduler: WorkManagerScheduler

    override fun onCreate() {
        super.onCreate()

        // Schedule background work
        workManagerScheduler.scheduleNewsRefresh()
        workManagerScheduler.scheduleBreakingNewsNotification()
    }
}
