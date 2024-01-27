package com.espressodev.gptmap.api.chatgpt

import com.espressodev.gptmap.core.model.Location

interface ChatgptService {
    suspend fun getPrompt(message: String): Result<Location>
}
