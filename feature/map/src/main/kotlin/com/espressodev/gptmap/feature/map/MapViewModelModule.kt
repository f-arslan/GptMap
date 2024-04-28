package com.espressodev.gptmap.feature.map

import android.content.Context
import com.espressodev.gptmap.core.data.repository.FavouriteRepository
import com.espressodev.gptmap.core.data.repository.ImageAnalysisRepository
import com.espressodev.gptmap.core.data.repository.UserRepository
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.firebase.FirestoreRepository
import com.espressodev.gptmap.core.gemini.GeminiRepository
import com.espressodev.gptmap.core.mongodb.FavouriteRealmRepository
import com.espressodev.gptmap.core.unsplash.UnsplashRepository
import com.espressodev.gptmap.feature.screenshot.ScreenshotServiceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun provideApiService(
        geminiRepository: GeminiRepository,
        unsplashRepository: UnsplashRepository
    ): ApiService = ApiService(geminiRepository, unsplashRepository)

    @ViewModelScoped
    @Provides
    fun provideRepositoryBundle(
        favouriteRepository: FavouriteRepository,
        userRepository: UserRepository,
        imageAnalysisRepository: ImageAnalysisRepository
    ): RepositoryBundle = RepositoryBundle(
        favouriteRepository,
        userRepository,
        imageAnalysisRepository
    )

    @ViewModelScoped
    @Provides
    fun provideDataService(
        favouriteRealmRepository: FavouriteRealmRepository,
        firestoreRepository: FirestoreRepository,
        dataStoreService: DataStoreService
    ): DataService = DataService(favouriteRealmRepository, firestoreRepository, dataStoreService)

    @ViewModelScoped
    @Provides
    fun screenshotServiceHandler(@ApplicationContext context: Context): ScreenshotServiceHandler =
        ScreenshotServiceHandler(context)
}

data class ApiService(
    val geminiRepository: GeminiRepository,
    val unsplashRepository: UnsplashRepository
)

data class RepositoryBundle(
    val favouriteRepository: FavouriteRepository,
    val userRepository: UserRepository,
    val imageAnalysisRepository: ImageAnalysisRepository
)

data class DataService(
    val favouriteRealmRepository: FavouriteRealmRepository,
    val firestoreRepository: FirestoreRepository,
    val dataStoreService: DataStoreService
)
