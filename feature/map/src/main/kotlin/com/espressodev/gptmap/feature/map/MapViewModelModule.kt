package com.espressodev.gptmap.feature.map

import android.content.Context
import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.core.common.DataStoreService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.domain.AddDatabaseIfUserIsNewUseCase
import com.espressodev.gptmap.core.domain.GetCurrentLocationUseCase
import com.espressodev.gptmap.core.domain.ImageToAnalysisUseCase
import com.espressodev.gptmap.core.domain.SaveImageToFirebaseStorageUseCase
import com.espressodev.gptmap.core.mongodb.FavouriteService
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
        geminiService: GeminiService,
        unsplashService: UnsplashService
    ): ApiService = ApiService(geminiService, unsplashService)

    @ViewModelScoped
    @Provides
    fun provideUseCaseBundle(
        saveImageToFirebaseStorageUseCase: SaveImageToFirebaseStorageUseCase,
        addDatabaseIfUserIsNewUseCase: AddDatabaseIfUserIsNewUseCase,
        getCurrentLocationUseCase: GetCurrentLocationUseCase,
        imageToAnalysisUseCase: ImageToAnalysisUseCase
    ): UseCaseBundle = UseCaseBundle(
        saveImageToFirebaseStorageUseCase,
        addDatabaseIfUserIsNewUseCase,
        getCurrentLocationUseCase,
        imageToAnalysisUseCase
    )

    @ViewModelScoped
    @Provides
    fun provideDataService(
        favouriteService: FavouriteService,
        firestoreService: FirestoreService,
        dataStoreService: DataStoreService
    ): DataService = DataService(favouriteService, firestoreService, dataStoreService)

    @ViewModelScoped
    @Provides
    fun screenshotServiceHandler(@ApplicationContext context: Context): ScreenshotServiceHandler =
        ScreenshotServiceHandler(context)
}

data class ApiService(
    val geminiService: GeminiService,
    val unsplashService: UnsplashService
)

data class UseCaseBundle(
    val saveImageToFirebaseStorageUseCase: SaveImageToFirebaseStorageUseCase,
    val addDatabaseIfUserIsNewUseCase: AddDatabaseIfUserIsNewUseCase,
    val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    val imageToAnalysisUseCase: ImageToAnalysisUseCase
)

data class DataService(
    val favouriteService: FavouriteService,
    val firestoreService: FirestoreService,
    val dataStoreService: DataStoreService
)
