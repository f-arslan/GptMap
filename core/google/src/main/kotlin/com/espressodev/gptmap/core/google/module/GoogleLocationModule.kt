package com.espressodev.gptmap.core.google.module

import android.content.Context
import com.espressodev.gptmap.core.google.GoogleLocationService
import com.espressodev.gptmap.core.google.impl.GoogleLocationServiceImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleLocationModule {

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(
        @ApplicationContext
        context: Context
    ) = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun providesGoogleLocationService(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): GoogleLocationService = GoogleLocationServiceImpl(fusedLocationProviderClient, context)
}
