package com.espressodev.gptmap.core.google.module

import android.content.Context
import com.espressodev.gptmap.core.google.GoogleLocationService
import com.espressodev.gptmap.core.google.impl.GoogleLocationServiceImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object GoogleLocationModule {

    @Provides
    @ViewModelScoped
    fun providesFusedLocationProviderClient(
        @ApplicationContext
        context: Context
    ) = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @ViewModelScoped
    fun providesGoogleLocationService(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): GoogleLocationService = GoogleLocationServiceImpl(fusedLocationProviderClient, context)
}
