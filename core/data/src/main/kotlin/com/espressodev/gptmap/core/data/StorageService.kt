package com.espressodev.gptmap.core.data

interface StorageService {
    suspend fun uploadImage(image: ByteArray, imageName: String, bucketName: String): Result<String>
}