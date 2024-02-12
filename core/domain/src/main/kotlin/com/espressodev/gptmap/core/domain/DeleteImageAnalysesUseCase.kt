package com.espressodev.gptmap.core.domain

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.espressodev.gptmap.core.common.DataStoreService
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.worker.DeleteImagesFromStorageAndPhoneWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteImageAnalysesUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val realmSyncService: RealmSyncService,
    private val dataStoreService: DataStoreService,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(imageIds: Set<String>) = runCatchingWithContext(ioDispatcher) {
        dataStoreService.setLatestImageIdForChat("")

        val deleteFromRealmJob = launch {
            realmSyncService.deleteImageAnalyses(imageIds).getOrThrow()
        }
        deleteFromRealmJob.join()

        val inputData =
            Data.Builder().putStringArray("imageIds", imageIds.toTypedArray()).build()
        val workRequest = OneTimeWorkRequestBuilder<DeleteImagesFromStorageAndPhoneWorker>()
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
        Unit
    }
}

