package com.espressodev.gptmap.core.chatgpt

import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import com.espressodev.gptmap.core.model.chatgpt.ChatgptResponse

interface ChatgptService {
    suspend fun getPrompt(message: ChatgptRequest): ChatgptResponse
}