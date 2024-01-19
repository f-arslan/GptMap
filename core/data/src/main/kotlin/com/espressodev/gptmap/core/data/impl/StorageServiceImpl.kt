package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.model.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val storage: FirebaseStorage) :
    StorageService {
    override suspend fun uploadImage(
        image: ByteArray,
        imageId: String,
        bucketName: String
    ): Result<String> =
        runCatching {
            val imageReference = storage.reference.child(bucketName).child(imageId)
            imageReference.putBytes(image).await()
            imageReference.downloadUrl.await().toString()
        }
    companion object {
        const val IMAGE_REFERENCE = "images"
        const val ANALYSIS_IMAGE_REFERENCE = "analysis"
    }
}
