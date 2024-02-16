package com.espressodev.gptmap.api.chatgpt.module

import com.espressodev.gptmap.api.chatgpt.BuildConfig.OPENAI_API_KEY
import com.espressodev.gptmap.api.chatgpt.ChatgptApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatgptModule {
    private const val BASE_URL = "https://api.openai.com/v1/chat/"
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request =
            chain.request().newBuilder().addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $OPENAI_API_KEY").build()
        chain.proceed(newRequest)
    }.build()

    @Provides
    @Singleton
    fun provideChatgptApiService(): ChatgptApi = Retrofit.Builder().client(client).baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(ChatgptApi::class.java)
}
