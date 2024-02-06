package com.espressodev.gptmap.core.common.impl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "Speech recognition is ready.")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "Speech recognition has begun.")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d("SpeechRecognizer", "RMS has changed: $rmsdB")
//                lastRmsValue = rmsdB.roundToInt()
//                trySend(SpeechRecognitionResult(emptyList(), lastRmsValue))
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SpeechRecognizer", "Buffer received: $buffer")
            }

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "Speech recognition has ended.")
            }

            override fun onError(error: Int) {
                trySend(SpeechRecognitionResult(emptyList(), lastRmsValue, true))
                Log.e("SpeechRecognizer", "Error occurred during speech recognition: $error")
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.let { recognizedText ->
                        trySend(SpeechRecognitionResult(recognizedText, lastRmsValue))
                    } ?: trySend(SpeechRecognitionResult(emptyList(), lastRmsValue, true))
                    .also {
                        Log.d("SpeechRecognizer", "Speech recognition results received: $it")
                    }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.let { recognizedText ->
                        trySend(SpeechRecognitionResult(recognizedText, lastRmsValue))
                    }.also {
                        Log.d(
                            "SpeechRecognizer",
                            "Partial speech recognition results received: $it"
                        )
                    }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
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

