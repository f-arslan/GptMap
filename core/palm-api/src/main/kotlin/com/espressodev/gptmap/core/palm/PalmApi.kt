package com.espressodev.gptmap.core.palm

import com.espressodev.gptmap.core.model.palm.PalmText
import com.espressodev.gptmap.core.model.palm.PalmTextPrompt
import com.espressodev.gptmap.core.model.palm.PalmTextResponse
import com.espressodev.gptmap.core.palm_api.BuildConfig.PALM_API_KEY
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.setBody

class PalmApi(private val client: HttpClient) {
    suspend fun generateText(prompt: String): PalmTextResponse {
        val palmTextPrompt = PalmTextPrompt(prompt = PalmText(prompt))

        return client.post("$BASE_URL$PALM_TEXT_MODEL$PALM_API_KEY") {
            headers { append("Content-Type", "application/json") }
            setBody(palmTextPrompt)
        }.body()
    }

    private companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta2/models/"
        const val PALM_TEXT_MODEL = "text-bison-001:generateText?key="
    }
}
