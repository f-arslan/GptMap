package com.espressodev.gptmap.api.unsplash

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.espressodev.gptmap.api.unsplash.impl.UnsplashServiceImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.espressodev.gptmap.api.unsplash.BuildConfig.UNSPLASH_BASE_URL
import com.google.firebase.FirebaseApp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class UnsplashServiceImplTest {

    private lateinit var unsplashService: UnsplashService
    private val token = "DEFINE_TEMP_TOKEN"
    @Before
    fun setUp() {
        runBlocking {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val newRequestBuilder = request.newBuilder()
                        .header("Authorization", "Bearer $token")
                    chain.proceed(newRequestBuilder.build())
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val unsplashApi = retrofit.create(UnsplashApi::class.java)
            unsplashService = UnsplashServiceImpl(unsplashApi)
        }
    }

    @Test
    fun getTwoPhotosReturnsSuccessResult() = runBlocking {
        val query = "nature"

        val result = unsplashService.getTwoPhotos(query)

        assert(result.isSuccess)
        val photos = result.getOrNull()
        assert(photos != null)
        assert(photos!!.isNotEmpty())
    }
}