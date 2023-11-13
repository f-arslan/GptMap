package com.espressodev.gptmap.core.chatgpt.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.espressodev.gptmap.core.chatgpt.BuildConfig.CHATGPT_KEY
import com.espressodev.gptmap.core.chatgpt.ChatgptApiService
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GptModule {
    private const val BASE_URL = "https://api.openai.com/v1/"
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request =
            chain.request().newBuilder().addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $CHATGPT_KEY").build()
        chain.proceed(newRequest)
    }.build()

    @Provides
    @Singleton
    fun provideChatgptApiService(): ChatgptApiService = Retrofit.Builder().client(client).baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(ChatgptApiService::class.java)
}