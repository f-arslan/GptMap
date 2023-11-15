package com.espressodev.gptmap.core.chatgpt.ext

import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest


internal fun ChatgptRequest.mergeMessageWithPreText(): ChatgptRequest {
    val currentContent = messages.first().content
    val requestContent = "${PRE_TEXT}$currentContent"
    val newMessages = listOf(messages.first().copy(content = requestContent))
    return copy(messages = newMessages)
}



const val PRE_TEXT = "Information: Details about a preferred location\n" +
        "Task: Provide coordinates, city, country, and a friendly description\n" +
        "Note: I can make similar guesses if specific information is not provided\n" +
        "Response Format: JSON object\n" +
        "Friendly Description: Keep the description warm and inviting and short (max 15 words)\n" +
        "Size of Description: Concise and brief\n" +
        "Output: JSON object only, no additional information provided in the response\n"