package com.droidnotes.core.network.di

import com.droidnotes.core.network.ApiKeyProvider
import com.droidnotes.core.network.BuildConfig
import com.droidnotes.core.network.BuildConfigApiKeyProvider
import com.droidnotes.core.network.GNewsApi
import com.droidnotes.core.network.NetworkConstant
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyProvider: ApiKeyProvider): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url
            val apiKey = apiKeyProvider.getApiKey()
            val url = if (apiKey != null) {
                originalHttpUrl.newBuilder()
                    .addQueryParameter("apikey", apiKey)
                    .build()
            } else {
                originalHttpUrl
            }
            val requestBuilder = original.newBuilder().url(url)
            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstant.GNEWS_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideGNewsApi(retrofit: Retrofit): GNewsApi {
        return retrofit.create(GNewsApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiKeyProviderModule {

    @Binds
    abstract fun bindApiKeyProvider(
        buildConfigApiKeyProvider: BuildConfigApiKeyProvider
    ): ApiKeyProvider
}
