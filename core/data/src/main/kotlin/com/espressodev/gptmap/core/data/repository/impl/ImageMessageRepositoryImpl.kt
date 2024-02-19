package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import com.espressodev.gptmap.core.data.di.Dispatcher
import com.espressodev.gptmap.core.data.di.GmDispatchers.IO
import com.espressodev.gptmap.core.data.repository.ImageMessageRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.gemini.GeminiDataSource
import com.espressodev.gptmap.core.model.Constants
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.ext.readBitmapFromExternalStorage
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import com.espressodev.gptmap.core.mongodb.ImageMessageDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageMessageRepositoryImpl @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val geminiDataSource: GeminiDataSource,
    private val imageMessageDataSource: ImageMessageDataSource
) : ImageMessageRepository {
    override suspend fun addImageMessage(imageId: String, text: String): Result<Unit> =
        runCatchingWithContext(ioDispatcher) {
            val realmImageMessage = RealmImageMessage().apply {
                request = text
            }

            launch {
                imageMessageDataSource.addImageMessageToImageAnalysis(
                    imageAnalysisId = imageId,
                    message = realmImageMessage
                ).getOrThrow()
            }

            val bitmap =
                context.readBitmapFromExternalStorage(
                    directoryName = Constants.PHONE_IMAGE_DIR,
                    filename = imageId
                )
                    ?: throw Exceptions.FailedToReadBitmapFromExternalStorageException()

            val stringBuilder = StringBuilder()

            runCatching {
                geminiDataSource.getImageDescription(bitmap = bitmap, text = text).getOrThrow()
                    .collect { chunk ->
                        stringBuilder.append(chunk)
                    }
            }.onFailure {
                stringBuilder.append(it.message)
            }

            val fullResponseText = stringBuilder.toString().trim()

            imageMessageDataSource.updateImageMessageInImageAnalysis(
                imageAnalysisId = imageId,
                messageId = realmImageMessage.id,
                text = fullResponseText,
            ).getOrThrow()
        }
}
