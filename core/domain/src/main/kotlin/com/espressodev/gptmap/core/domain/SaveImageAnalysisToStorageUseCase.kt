package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import android.util.Log
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.impl.StorageServiceImpl.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import io.realm.kotlin.exceptions.RealmException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class SaveImageAnalysisToStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val realmSyncService: RealmSyncService
) {
    suspend operator fun invoke(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        try {
            val byteArray = bitmapToByteArray(bitmap)
            val imageId = UUID.randomUUID().toString()
            val uploadImageResult = storageService.uploadImage(
                byteArray,
                imageId,
                ANALYSIS_IMAGE_REFERENCE
            ).getOrThrow()

            saveImageAnalysisToRealm(imageId, uploadImageResult)
            Result.success(true)
        } catch (e: IOException) {
            Log.e(classTag(), "I/O error while saving image", e)
            Result.failure(e)
        } catch (e: OutOfMemoryError) {
            Log.e(classTag(), "Out of memory error while saving image", e)
            Result.failure(e)
        } catch (e: RealmException) {
            Log.e(classTag(), "Realm error while saving image", e)
            Result.failure(e)
        }
    }

    private suspend fun saveImageAnalysisToRealm(imageId: String, imageUrl: String) {
        val realmImageAnalysis = RealmImageAnalysis().apply {
            this.imageId = imageId
            this.imageUrl = imageUrl
        }
        realmSyncService.saveImageAnalysis(realmImageAnalysis).getOrThrow()
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
