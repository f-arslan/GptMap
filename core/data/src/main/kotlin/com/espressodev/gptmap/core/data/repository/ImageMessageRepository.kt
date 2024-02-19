package com.espressodev.gptmap.core.data.repository

interface ImageMessageRepository {
    suspend fun addImageMessage(imageId: String, text: String): Result<Unit>
}
