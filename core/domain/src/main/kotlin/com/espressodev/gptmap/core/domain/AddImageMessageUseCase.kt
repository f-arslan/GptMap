package com.espressodev.gptmap.core.domain

import android.content.Context
import android.util.Log
import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.Constants.PHONE_IMAGE_DIR
import com.espressodev.gptmap.core.model.Exceptions.FailedToReadBitmapFromExternalStorageException
import com.espressodev.gptmap.core.model.ext.readBitmapFromExternalStorage
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddImageMessageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val geminiService: GeminiService,
    private val realmSyncService: RealmSyncService,
) {
    suspend operator fun invoke(imageId: String, text: String) =
        runCatchingWithContext(ioDispatcher) {
            val realmImageMessage = RealmImageMessage().apply {
                request = text
            }

            launch {
                realmSyncService.addImageMessageToImageAnalysis(
                    imageAnalysisId = imageId,
                    message = realmImageMessage
                ).getOrThrow()

            }

            val bitmap =
                context.readBitmapFromExternalStorage(
                    directoryName = PHONE_IMAGE_DIR,
                    filename = imageId
                )
                    ?: throw FailedToReadBitmapFromExternalStorageException()

            val stringBuilder = StringBuilder()
            geminiService.getImageDescription(bitmap = bitmap, text = text)
                .onSuccess { chunkFlow ->
                    chunkFlow.collect { chunk ->
                        stringBuilder.append(chunk)
                    }
                }
                .onFailure { throwable ->
                    stringBuilder.append(throwable.message ?: "Something went wrong")
                    Log.e("AddImageMessageUseCase", "invoke: ", throwable)
                }
            val fullResponseText = stringBuilder.toString().trim()

            realmSyncService.updateImageMessageInImageAnalysis(
                imageAnalysisId = imageId,
                messageId = realmImageMessage.id,
                text = fullResponseText,
            ).getOrThrow()
        }
}

