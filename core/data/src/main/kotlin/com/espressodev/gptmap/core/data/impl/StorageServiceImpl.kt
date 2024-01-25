package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.StorageService
import com.google.firebase.storage.FirebaseStorage
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

    override suspend fun deleteImage(imageId: String, bucketName: String): Result<Unit> =
        runCatching {
            storage.reference.child(bucketName).child(imageId).delete().await()
        }
}
