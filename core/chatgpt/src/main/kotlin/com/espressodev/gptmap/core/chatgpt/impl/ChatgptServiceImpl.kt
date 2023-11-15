package com.espressodev.gptmap.core.chatgpt.impl

import com.espressodev.gptmap.core.chatgpt.ChatgptService
import com.espressodev.gptmap.core.chatgpt.ext.mergeMessageWithPreText
import com.espressodev.gptmap.core.chatgpt.module.ChatgptApi
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ChatgptServiceImpl @Inject constructor(private val chatgptService: ChatgptApi) :
    ChatgptService {
    override suspend fun getPrompt(message: ChatgptRequest): Response<Location> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    chatgptService.getLocationInformation(message.mergeMessageWithPreText())
                val content = response.choices[0].message.content
                val location = response.toLocation(id = response.id, responseContent = content)
                Response.Success(location)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
}