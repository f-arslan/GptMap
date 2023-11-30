package com.espressodev.gptmap.core.data.module

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun auth(): FirebaseAuth = Firebase.auth

    @Provides
    fun firestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Singleton
    @Provides
    fun context(@ApplicationContext context: Context): Context {
        return context.applicationContext
    }
}