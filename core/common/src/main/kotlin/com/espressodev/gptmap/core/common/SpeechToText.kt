package com.espressodev.gptmap.core.common

import kotlinx.coroutines.flow.Flow

interface SpeechToText {
    fun startListening(): Flow<SpeechRecognitionResult>
    fun stopListening()
}

data class SpeechRecognitionResult(
    val recognizedText: List<String>,
    val rmsValue: Int = 0,
    val isFinished: Boolean = false
)
