package com.droidnotes.data.news.di

import com.droidnotes.domain.news.repo.PagedNewsRepository
import com.droidnotes.domain.news.usecase.ToggleBookmark
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsDataProvider {

    @Provides
    @Singleton
    fun provideToggleBookMarkUseCase(repo: PagedNewsRepository): ToggleBookmark {
        return ToggleBookmark(repo)
    }
}