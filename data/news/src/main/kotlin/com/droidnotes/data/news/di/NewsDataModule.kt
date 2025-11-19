package com.droidnotes.data.news.di

import com.droidnotes.data.news.PagedNewsRepositoryImpl
import com.droidnotes.data.news.NewsRepositoryImpl
import com.droidnotes.data.news.local.NewsLocalDataSource
import com.droidnotes.data.news.local.NewsLocalDataSourceImpl
import com.droidnotes.data.news.remote.NewsRemoteDataSource
import com.droidnotes.data.news.remote.NewsRemoteDataSourceImpl
import com.droidnotes.domain.news.PagedNewsRepository
import com.droidnotes.domain.news.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NewsDataModule {
    @Binds
    @Singleton
    fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    fun bindPagedNewsRepository(impl: PagedNewsRepositoryImpl): PagedNewsRepository

    @Binds
    @Singleton
    fun bindNewsRemoteDataSource(impl: NewsRemoteDataSourceImpl): NewsRemoteDataSource

    @Binds
    @Singleton
    fun bindNewsLocalDataSource(impl: NewsLocalDataSourceImpl): NewsLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
