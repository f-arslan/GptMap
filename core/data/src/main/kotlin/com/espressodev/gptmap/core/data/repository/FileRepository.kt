package com.espressodev.gptmap.core.data.repository

interface FileRepository {
    suspend fun deleteFilesFromInternal(filenames: List<String>, dir: String): Result<Unit>
    suspend fun saveImageToInternal(imageUrl: String, fileId: String, size: Int): Result<Unit>
}