package com.espressodev.gptmap.core.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.data.repository.FileRepository
import com.espressodev.gptmap.core.firebase.StorageRepository
import com.espressodev.gptmap.core.firebase.StorageRepository.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.Constants

@HiltWorker
class DeleteImagesFromStorageAndPhoneWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val storageRepository: StorageRepository,
    private val fileRepository: FileRepository,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        val imageIds = inputData.getStringArray("imageIds")
            ?: throw IllegalArgumentException("No imageIds provided")

        imageIds.forEach { id ->
            storageRepository.deleteImage(id, ANALYSIS_IMAGE_REFERENCE).getOrThrow()
        }

        fileRepository.deleteFilesFromInternal(
            filenames = imageIds.toList(),
            Constants.PHONE_IMAGE_DIR
        ).getOrThrow()

        Result.success()
    } catch (e: Exception) {
        Log.e("DeleteImagesFromStorageAndPhoneWorker", "doWork: failure $e")
        Result.failure()
    }

    class Factory(
        private val storageRepository: StorageRepository,
        private val fileRepository: FileRepository,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ) = if (workerClassName == DeleteImagesFromStorageAndPhoneWorker::class.java.name) {
            DeleteImagesFromStorageAndPhoneWorker(
                appContext,
                workerParameters,
                storageRepository,
                fileRepository
            )
        } else {
            null
        }
    }
}
