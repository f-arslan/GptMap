package com.espressodev.gptmap.core.data

interface StorageService {

    suspend fun uploadImage(image: ByteArray, imageName: String): Result<String>

}