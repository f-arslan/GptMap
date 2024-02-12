package com.espressodev.gptmap.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.domain.DeleteFilesFromInternalUseCase
import com.espressodev.gptmap.core.model.Constants


@HiltWorker
class DeleteImagesFromStorageAndPhoneWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val storageService: StorageService,
    private val deleteFilesFromInternalUseCase: DeleteFilesFromInternalUseCase,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        val imageIds = inputData.getStringArray("imageIds")
            ?: throw IllegalArgumentException("No imageIds provided")

        imageIds.forEach { id ->
            storageService.deleteImage(id, StorageService.ANALYSIS_IMAGE_REFERENCE).getOrThrow()
        }

        deleteFilesFromInternalUseCase(filenames = imageIds.toList(), Constants.PHONE_IMAGE_DIR)
            .getOrThrow()

        Result.success()
    } catch (e: Exception) {
        Log.e("DeleteImagesFromStorageAndPhoneWorker", "doWork: failure $e")
        Result.failure()
    }

    class Factory(
        private val storageService: StorageService,
        private val deleteFilesFromInternalUseCase: DeleteFilesFromInternalUseCase,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ) = if (workerClassName == DeleteImagesFromStorageAndPhoneWorker::class.java.name) {
            DeleteImagesFromStorageAndPhoneWorker(
                appContext,
                workerParameters,
                storageService,
                deleteFilesFromInternalUseCase
            )
        } else {
            null
        }
    }
}
