package com.espressodev.gptmap.core.common.impl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.espressodev.gptmap.core.common.SpeechRecognitionResult
import com.espressodev.gptmap.core.common.SpeechToText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.math.roundToInt

class SpeechToTextImpl @Inject constructor(
    @ApplicationContext context: Context
) : SpeechToText {

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    override fun startListening(): Flow<SpeechRecognitionResult> = callbackFlow {
        var lastRmsValue = 0
        var lastUpdateTime = System.currentTimeMillis()
        val throttleDelay = 100L

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit

            override fun onBeginningOfSpeech() = Unit

            override fun onRmsChanged(rmsdB: Float) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTime >= throttleDelay) {
                    lastRmsValue = rmsdB.roundToInt()
                    trySend(SpeechRecognitionResult(emptyList(), lastRmsValue))
                    lastUpdateTime = currentTime
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() = Unit

            override fun onError(error: Int) {
                trySend(SpeechRecognitionResult(emptyList(), lastRmsValue, true))
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.let { recognizedText ->
                        trySend(SpeechRecognitionResult(recognizedText, lastRmsValue))
                    } ?: trySend(SpeechRecognitionResult(emptyList(), lastRmsValue, true))
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.let { recognizedText ->
                        trySend(SpeechRecognitionResult(recognizedText, lastRmsValue))
                    }
            }

            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }

        speechRecognizer.setRecognitionListener(listener)
        speechRecognizer.startListening(recognizerIntent)

        awaitClose {
            speechRecognizer.stopListening()
            speechRecognizer.setRecognitionListener(null)
            speechRecognizer.destroy()
        }
    }

    override fun stopListening() {
        speechRecognizer.stopListening()
    }

    private companion object {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000)
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                10000
            )
        }
    }
}
