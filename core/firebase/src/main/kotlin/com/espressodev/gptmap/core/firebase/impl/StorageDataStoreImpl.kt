package com.espressodev.gptmap.core.firebase.impl

import com.espressodev.gptmap.core.firebase.StorageDataStore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageDataStoreImpl @Inject constructor(private val storage: FirebaseStorage) :
    StorageDataStore {
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
