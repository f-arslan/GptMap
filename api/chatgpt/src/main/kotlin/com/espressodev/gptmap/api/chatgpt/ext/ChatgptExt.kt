package com.espressodev.gptmap.api.chatgpt.ext

import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import com.espressodev.gptmap.core.model.PromptUtil.locationPreText


internal fun ChatgptRequest.mergeMessageWithPreText(): ChatgptRequest {
    val currentContent = messages.first().content
    val requestContent = "${locationPreText}$currentContent"
    val newMessages = listOf(messages.first().copy(content = requestContent))
    return copy(messages = newMessages)
}