package com.espressodev.gptmap.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.datastore.UserPreferences
import com.espressodev.gptmap.core.datastore.UserPreferencesSerializer
import com.espressodev.gptmap.core.datastore.impl.DataStoreServiceImpl
import com.espressodev.gptmap.core.model.di.ApplicationScope
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(GmDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
}


@Module
@InstallIn(SingletonComponent::class)
interface DataStoreBinder {
    @Binds
    fun provideDataStoreService(impl: DataStoreServiceImpl): DataStoreService
}
