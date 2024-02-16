package com.espressodev.gptmap.api.unsplash.module

import android.util.Log
import com.espressodev.gptmap.api.unsplash.BuildConfig.UNSPLASH_BASE_URL
import com.espressodev.gptmap.api.unsplash.UnsplashApi
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.api.unsplash.impl.UnsplashServiceImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object UnsplashModule {
    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val newRequestBuilder = request.newBuilder()
                    .addHeader("Content-Type", "application/json")

                val token = runBlocking { getFirebaseToken() }

                if (token != null) {
                    newRequestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(newRequestBuilder.build())
            }
            .build()
    }

    private suspend fun getFirebaseToken(): String? {
        return try {
            FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            Log.e("UnsplashModule", "getFirebaseToken: ", e)
            null
        }
    }

    @ViewModelScoped
    @Provides
    fun provideUnsplashApi(okHttpClient: OkHttpClient): UnsplashApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashApi::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideUnsplashService(unsplashApi: UnsplashApi): UnsplashService =
        UnsplashServiceImpl(unsplashApi)
}
