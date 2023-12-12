package com.espressodev.gptmap.core.unsplash_api.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import com.espressodev.gptmap.core.unsplash_api.BuildConfig.UNSPLASH_API_KEY
import com.espressodev.gptmap.core.unsplash_api.UnsplashApi
import com.espressodev.gptmap.core.unsplash_api.UnsplashService
import com.espressodev.gptmap.core.unsplash_api.impl.UnsplashServiceImpl
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UnsplashModule {
    private const val BASE_URL = "https://api.unsplash.com/"
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request =
            chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Client-ID $UNSPLASH_API_KEY")
                .addHeader("Accept-Version", "v1").build()
        chain.proceed(newRequest)
    }.build()

    @Provides
    @Singleton
    fun provideUnsplashApi(): UnsplashApi = Retrofit.Builder().client(client).baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(UnsplashApi::class.java)

    @Provides
    @Singleton
    fun provideUnsplashService(unsplashApi: UnsplashApi): UnsplashService = UnsplashServiceImpl(unsplashApi)
}