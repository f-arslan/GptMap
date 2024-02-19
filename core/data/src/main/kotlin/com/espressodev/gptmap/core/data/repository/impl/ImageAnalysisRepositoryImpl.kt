package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import android.graphics.Bitmap
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.espressodev.gptmap.core.data.di.Dispatcher
import com.espressodev.gptmap.core.data.di.GmDispatchers.IO
import com.espressodev.gptmap.core.data.repository.ImageAnalysisRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.data.worker.DeleteImagesFromStorageAndPhoneWorker
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.firebase.StorageDataStore
import com.espressodev.gptmap.core.model.Constants
import com.espressodev.gptmap.core.model.ImageType
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.downloadResizeAndCompress
import com.espressodev.gptmap.core.model.ext.resizeImage
import com.espressodev.gptmap.core.model.ext.saveToInternalStorageIfNotExist
import com.espressodev.gptmap.core.model.ext.toBitmap
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.mongodb.ImageAnalysisDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ImageAnalysisRepositoryImpl @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val imageAnalysisDataSource: ImageAnalysisDataSource,
    private val dataStoreService: DataStoreService,
    private val storageDataStore: StorageDataStore
) : ImageAnalysisRepository {
    override suspend fun deleteImageAnalyses(imageIds: Set<String>): Result<Unit> =
        runCatchingWithContext(ioDispatcher) {
            dataStoreService.setLatestImageIdForChat("")

            val deleteFromRealmJob = launch {
                imageAnalysisDataSource.deleteImageAnalyses(imageIds).getOrThrow()
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

    override suspend fun turnImageToImageAnalysis(imageUrl: String): Result<String> =
        runCatchingWithContext(ioDispatcher) {
            val bitmap =
                imageUrl.downloadResizeAndCompress(
                    width = Constants.DOWNLOAD_IMAGE_WIDTH_FOR_ANALYSIS,
                    height = Constants.DOWNLOAD_IMAGE_HEIGHT_FOR_ANALYSIS
                ).toBitmap()

            val imageAnalysisId = saveImageAnalysisToStorage(
                bitmap = bitmap,
                title = "",
                imageWidth = Constants.DOWNLOAD_IMAGE_WIDTH_FOR_ANALYSIS,
                imageHeight = Constants.DOWNLOAD_IMAGE_HEIGHT_FOR_ANALYSIS,
                imageType = ImageType.Favourite
            ).getOrThrow()

            launch {
                dataStoreService.setLatestImageIdForChat(imageAnalysisId)
            }
            launch {
                dataStoreService.setImageUrl(imageUrl)
            }
            launch {
                bitmap.saveToInternalStorageIfNotExist(context, imageAnalysisId)
            }
            imageAnalysisId
        }

    override suspend fun saveImageAnalysisToStorage(
        bitmap: Bitmap,
        title: String,
        imageWidth: Int,
        imageHeight: Int,
        imageType: ImageType
    ): Result<String> = runCatchingWithContext(ioDispatcher) {
        val byteArray =
            bitmap.resizeImage(imageWidth, imageHeight).compressImage()
        val imageId = UUID.randomUUID().toString()
        val imageUrl = storageDataStore.uploadImage(
            byteArray,
            imageId,
            StorageDataStore.ANALYSIS_IMAGE_REFERENCE
        ).getOrThrow()
        saveImageAnalysisToRealm(imageId, imageUrl, title, imageType)
        imageId
    }

    private suspend fun saveImageAnalysisToRealm(
        imageId: String,
        imageUrl: String,
        title: String,
        imageType: ImageType
    ) {
        val realmImageAnalysis = RealmImageAnalysis().apply {
            this.imageId = imageId
            this.imageUrl = imageUrl
            this.title = title
            this.imageType = imageType.name
        }
        imageAnalysisDataSource.saveImageAnalysis(realmImageAnalysis).getOrThrow()
    }
}
