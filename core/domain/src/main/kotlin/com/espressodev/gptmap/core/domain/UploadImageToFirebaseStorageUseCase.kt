package com.espressodev.gptmap.core.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadImageToFirebaseStorageUseCase {

    suspend operator fun invoke() = withContext(Dispatchers.IO) {

    }
}