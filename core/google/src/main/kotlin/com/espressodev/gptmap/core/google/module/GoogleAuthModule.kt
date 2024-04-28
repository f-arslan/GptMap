package com.espressodev.gptmap.core.google.module

import android.content.Context
import androidx.credentials.CredentialManager
import com.espressodev.gptmap.core.google.BuildConfig.WEB_CLIENT_ID
import com.espressodev.gptmap.core.google.GoogleAuthService
import com.espressodev.gptmap.core.google.GoogleProfileService
import com.espressodev.gptmap.core.google.impl.GoogleAuthServiceImpl
import com.espressodev.gptmap.core.google.impl.GoogleProfileServiceImpl
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoogleAuthModule {
    @Singleton
    @Provides
    fun provideGetGoogleIdOption() = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(WEB_CLIENT_ID)
        .build()

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Provides
    fun provideAuthService(
        auth: FirebaseAuth,
        googleIdOption: GetGoogleIdOption,
        credentialManager: CredentialManager
    ): GoogleAuthService = GoogleAuthServiceImpl(
        auth = auth,
        googleIdOption = googleIdOption,
        credentialManager = credentialManager
    )

    @Provides
    fun provideProfileService(
        auth: FirebaseAuth,
        credentialManager: CredentialManager,
    ): GoogleProfileService = GoogleProfileServiceImpl(
        auth = auth,
        credentialManager = credentialManager
    )
}
