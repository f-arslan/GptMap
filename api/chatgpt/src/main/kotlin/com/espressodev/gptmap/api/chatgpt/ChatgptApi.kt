package com.espressodev.gptmap.api.chatgpt

import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import com.espressodev.gptmap.core.model.chatgpt.ChatgptResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatgptApi {
    @POST("completions")
    suspend fun getLocationInformation(@Body body: ChatgptRequest): ChatgptResponse
}