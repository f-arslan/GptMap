package com.espressodev.gptmap

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.domain.DeleteFilesFromInternalUseCase
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.UserManagementService
import com.espressodev.gptmap.core.worker.DeleteImagesFromStorageAndPhoneWorker
import com.espressodev.gptmap.core.worker.DeleteUserFromRealmWorker
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
    storageService: StorageService,
    deleteFilesFromInternalUseCase: DeleteFilesFromInternalUseCase,
    userManagementService: UserManagementService,
    realmAccountService: RealmAccountService,
) : DelegatingWorkerFactory() {
    init {
        addFactory(
            DeleteImagesFromStorageAndPhoneWorker.Factory(
                storageService = storageService,
                deleteFilesFromInternalUseCase = deleteFilesFromInternalUseCase
            )
        )
        addFactory(
            DeleteUserFromRealmWorker.Factory(
                userManagementService = userManagementService,
                realmAccountService = realmAccountService
            )
        )
    }
}
