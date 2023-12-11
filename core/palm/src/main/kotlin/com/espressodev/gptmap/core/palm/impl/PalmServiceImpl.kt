package com.espressodev.gptmap.core.palm.impl

import android.util.Log
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.palm.PalmService
import com.google.ai.generativelanguage.v1beta3.GenerateTextRequest
import com.google.ai.generativelanguage.v1beta3.TextPrompt
import com.google.ai.generativelanguage.v1beta3.TextServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PalmServiceImpl @Inject constructor(private val textServiceClient: TextServiceClient) :
    PalmService {

    override suspend fun getLocationInfo(textContent: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val prompt = createPrompt(textContent)
                val textRequest = createTextRequest(prompt)
                val response = generateText(textRequest)
                Log.d(classTag(), response)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    private fun createPrompt(textContent: String) =
        TextPrompt.newBuilder()
            .setText(textContent)
            .build()


    private fun createTextRequest(prompt: TextPrompt): GenerateTextRequest {
        return GenerateTextRequest.newBuilder()
            .setModel("models/text-bison-001")
            .setPrompt(prompt)
            .setTemperature(0.2f)
            .setCandidateCount(1)
            .build()
    }

    fun generateText(request: GenerateTextRequest): String {
        val response = textServiceClient.generateText(request)
        val responseText = response.candidatesList.last()
        return responseText.output
    }
}