package com.espressodev.gptmap

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.espressodev.gptmap.core.data.repository.FileRepository
import com.espressodev.gptmap.core.data.worker.DeleteImagesFromStorageAndPhoneWorker
import com.espressodev.gptmap.core.data.worker.DeleteUserFromRealmWorker
import com.espressodev.gptmap.core.firebase.StorageRepository
import com.espressodev.gptmap.core.mongodb.RealmAccountRepository
import com.espressodev.gptmap.core.mongodb.UserManagementRealmRepository
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class GmHiltApp : Application(), Configuration.Provider {

    @Inject
    lateinit var gptmapWorkersFactory: GptmapWorkersFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(gptmapWorkersFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}


class GptmapWorkersFactory @Inject constructor(
    storageRepository: StorageRepository,
    fileRepository: FileRepository,
    userManagementRealmRepository: UserManagementRealmRepository,
    realmAccountRepository: RealmAccountRepository,
) : DelegatingWorkerFactory() {
    init {
        addFactory(
            DeleteImagesFromStorageAndPhoneWorker.Factory(
                storageRepository = storageRepository,
                fileRepository = fileRepository
            )
        )
        addFactory(
            DeleteUserFromRealmWorker.Factory(
                userManagementRealmRepository = userManagementRealmRepository,
                realmAccountRepository = realmAccountRepository
            )
        )
    }
}
