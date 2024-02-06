package com.espressodev.gptmap.core.domain

import android.content.Context
import android.util.Log
import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.core.model.Exceptions.FailedToReadBitmapFromExternalStorageException
import com.espressodev.gptmap.core.model.ext.readBitmapFromExternalStorage
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddImageMessageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val geminiService: GeminiService,
    private val realmSyncService: RealmSyncService,
) {

    suspend operator fun invoke(imageId: String, text: String) = withContext(ioDispatcher) {
        runCatching {
            val bitmap =
                context.readBitmapFromExternalStorage(directoryName = "images", filename = imageId)
                    ?: throw FailedToReadBitmapFromExternalStorageException()

            val stringBuilder = StringBuilder()
            geminiService.getImageDescription(bitmap = bitmap, text = text).collect { chunk ->
                stringBuilder.append(chunk)
            }
            val fullResponseText = stringBuilder.toString()
            val realmImageMessage = RealmImageMessage().apply {
                request = text
                response = fullResponseText
            }
            realmSyncService.addImageMessageToImageAnalysis(
                imageAnalysisId = imageId,
                message = realmImageMessage
            ).getOrThrow()
        }
    }
}
