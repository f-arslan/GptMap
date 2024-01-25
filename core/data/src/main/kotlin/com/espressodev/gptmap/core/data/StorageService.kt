package com.espressodev.gptmap.core.data

interface StorageService {
    suspend fun uploadImage(image: ByteArray, imageId: String, bucketName: String): Result<String>
    suspend fun deleteImage(imageId: String, bucketName: String): Result<Unit>
    companion object {
        const val IMAGE_REFERENCE = "images"
        const val ANALYSIS_IMAGE_REFERENCE = "analysis"
    }
}
