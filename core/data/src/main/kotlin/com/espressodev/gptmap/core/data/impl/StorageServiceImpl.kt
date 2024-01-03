package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.StorageService
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutionException
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val storage: FirebaseStorage) :
    StorageService {

    override suspend fun uploadImage(
        image: ByteArray,
        imageName: String,
        bucketName: String
    ): Result<String> =
        try {
            val imageReference = storage.reference.child(bucketName).child(imageName)
            imageReference.putBytes(image).await()
            Result.success(imageReference.downloadUrl.await().toString().also(::println))
        } catch (e: StorageException) {
            Result.failure(e)
        } catch (e: InterruptedException) {
            Result.failure(e)
        } catch (e: ExecutionException) {
            Result.failure(e)
        }

    companion object {
        const val IMAGE_REFERENCE = "images"
        const val ANALYSIS_IMAGE_REFERENCE = "analysis"
    }
}
