package com.espressodev.gptmap.api.palm.module

import com.espressodev.gptmap.api.palm.PalmApi
import com.espressodev.gptmap.api.palm.PalmService
import com.espressodev.gptmap.api.palm.impl.PalmServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PalmModule {

    @Provides
    @Singleton
    fun providePalmService(palmApi: PalmApi): PalmService = PalmServiceImpl(palmApi)

    @Provides
    @Singleton
    fun provideKtorClient() = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
    }

    @Provides
    @Singleton
    fun providePalmApi(
        client: HttpClient
    ): PalmApi = PalmApi(client)
}
