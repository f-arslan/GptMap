package com.espressodev.gptmap.api.unsplash.module

import com.espressodev.gptmap.api.unsplash.UnsplashApi
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.api.unsplash.impl.UnsplashServiceImpl
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.espressodev.gptmap.api.unsplash.BuildConfig.UNSPLASH_API_KEY

@Module
@InstallIn(SingletonComponent::class)
object UnsplashModule {
    private const val BASE_URL = "http://127.0.0.1:8080/"

    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request =
            chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
        chain.proceed(newRequest)
    }.build()

    @Provides
    @Singleton
    fun provideUnsplashApi(): UnsplashApi = Retrofit.Builder().client(client).baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(UnsplashApi::class.java)

    @Provides
    @Singleton
    fun provideUnsplashService(unsplashApi: UnsplashApi): UnsplashService =
        UnsplashServiceImpl(unsplashApi)
}