package com.espressodev.gptmap.core.data

interface StorageService {
    suspend fun uploadImage(image: ByteArray, imageId: String, bucketName: String): Result<String>
}
