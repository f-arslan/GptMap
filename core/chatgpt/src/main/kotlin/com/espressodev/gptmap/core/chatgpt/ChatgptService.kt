package com.espressodev.gptmap.core.chatgpt

import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest

interface ChatgptService {
    suspend fun getPrompt(message: String): Result<Location>
}