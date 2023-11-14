package com.espressodev.gptmap.core.chatgpt.impl

import com.espressodev.gptmap.core.chatgpt.ChatgptService
import com.espressodev.gptmap.core.chatgpt.module.ChatgptApi
import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import com.espressodev.gptmap.core.model.chatgpt.ChatgptResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ChatgptServiceImpl @Inject constructor(private val chatgptService: ChatgptApi) :
    ChatgptService {
    override suspend fun getPrompt(message: ChatgptRequest): ChatgptResponse =
        withContext(Dispatchers.IO) {
            chatgptService.getLocationInformation(message)
        }
}