package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.StorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadImageToFirebaseStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase
) {
    suspend operator fun invoke(imageUrl: String, fileId: String) = withContext(Dispatchers.IO) {
        try {
            downloadAndCompressImageUseCase(imageUrl).onSuccess { imageData ->
                storageService.uploadImage(imageData, fileId)
                saveImageUrlToRealm(imageUrl)
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveImageUrlToRealm(imageUrl: String) {

    }
}