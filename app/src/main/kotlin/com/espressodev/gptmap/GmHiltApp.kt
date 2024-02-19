package com.espressodev.gptmap

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.espressodev.gptmap.core.data.repository.FileRepository
import com.espressodev.gptmap.core.data.worker.DeleteImagesFromStorageAndPhoneWorker
import com.espressodev.gptmap.core.data.worker.DeleteUserFromRealmWorker
import com.espressodev.gptmap.core.firebase.StorageDataStore
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.UserManagementDataSource
import dagger.hilt.android.HiltAndroidApp
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
}


class GptmapWorkersFactory @Inject constructor(
    storageDataStore: StorageDataStore,
    fileRepository: FileRepository,
    userManagementDataSource: UserManagementDataSource,
    realmAccountService: RealmAccountService,
) : DelegatingWorkerFactory() {
    init {
        addFactory(
            DeleteImagesFromStorageAndPhoneWorker.Factory(
                storageDataStore = storageDataStore,
                fileRepository = fileRepository
            )
        )
        addFactory(
            DeleteUserFromRealmWorker.Factory(
                userManagementDataSource = userManagementDataSource,
                realmAccountService = realmAccountService
            )
        )
    }
}
