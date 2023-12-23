package com.espressodev.gptmap.core.data.impl

import com.espressodev.gptmap.core.data.StorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val storage: FirebaseStorage) :
    StorageService {

    override suspend fun uploadImage(image: ByteArray, imageName: String): String {
        val imageReference = storage.reference.child(IMAGE_REFERENCE).child(imageName)
        imageReference.putBytes(image).await()
        return imageReference.downloadUrl.await().toString()
    }

    companion object {
        private const val IMAGE_REFERENCE = "images"
    }
}