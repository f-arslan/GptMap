package com.espressodev.gptmap.api.unsplash.module

import com.espressodev.gptmap.api.unsplash.UnsplashApi
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.api.unsplash.impl.UnsplashServiceImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.espressodev.gptmap.api.unsplash.BuildConfig.UNSPLASH_BASE_URL
@Module
@InstallIn(ViewModelComponent::class)
object UnsplashModule {
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request =
            chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.result?.token}")
                .build()
        chain.proceed(newRequest)
    }.build()

    @ViewModelScoped
    @Provides
    fun provideUnsplashApi(): UnsplashApi = Retrofit.Builder().client(client).baseUrl(UNSPLASH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(UnsplashApi::class.java)

    @Provides
    @ViewModelScoped
    fun provideUnsplashService(unsplashApi: UnsplashApi): UnsplashService =
        UnsplashServiceImpl(unsplashApi)
}