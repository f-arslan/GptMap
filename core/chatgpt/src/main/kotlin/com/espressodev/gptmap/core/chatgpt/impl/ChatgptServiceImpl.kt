package com.espressodev.gptmap.core.chatgpt.impl

import android.util.Log
import com.espressodev.gptmap.core.chatgpt.ChatgptService
import com.espressodev.gptmap.core.chatgpt.ext.mergeMessageWithPreText
import com.espressodev.gptmap.core.chatgpt.ext.toLocation
import com.espressodev.gptmap.core.chatgpt.module.ChatgptApi
import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ChatgptServiceImpl @Inject constructor(private val chatgptService: ChatgptApi) :
    ChatgptService {
    override suspend fun getPrompt(message: ChatgptRequest) = withContext(Dispatchers.IO) {
        val response = chatgptService.getLocationInformation(message.mergeMessageWithPreText())
        val content = response.choices[0].message.content
        response.toLocation(id = response.id, responseContent = content)
    }

}