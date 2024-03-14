package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import com.espressodev.gptmap.core.data.repository.ImageMessageRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.gemini.GeminiRepository
import com.espressodev.gptmap.core.model.Constants
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.ext.readBitmapFromExternalStorage
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import com.espressodev.gptmap.core.mongodb.ImageMessageRealmRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageMessageRepositoryImpl @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val geminiRepository: GeminiRepository,
    private val imageMessageRealmRepository: ImageMessageRealmRepository
) : ImageMessageRepository {
    override suspend fun addImageMessage(imageId: String, text: String): Result<Unit> =
        runCatchingWithContext(ioDispatcher) {
            val realmImageMessage = RealmImageMessage().apply {
                request = text
            }

            launch {
                imageMessageRealmRepository.addImageMessageToImageAnalysis(
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
            var totalToken = 0
            runCatching {
                geminiRepository.getImageDescription(bitmap = bitmap, text = text).getOrThrow()
                    .collect { (chunk, token) ->
                        stringBuilder.append(chunk)
                        totalToken = token.also(::println)
                    }
            }.onFailure {
                stringBuilder.append(it.message)
            }

            val fullResponseText = stringBuilder.toString().trim()
            imageMessageRealmRepository.updateImageMessageInImageAnalysis(
                imageAnalysisId = imageId,
                messageId = realmImageMessage.id,
                text = fullResponseText,
                token = totalToken
            ).getOrThrow()
        }
}
