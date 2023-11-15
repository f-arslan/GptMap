package com.espressodev.gptmap.core.chatgpt.ext

import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest


internal fun ChatgptRequest.mergeMessageWithPreText(): ChatgptRequest {
    val currentContent = messages.first().content
    val requestContent = "${PRE_TEXT}$currentContent"
    val newMessages = listOf(messages.first().copy(content = requestContent))
    return copy(messages = newMessages)
}


private const val PRE_TEXT = "Information: Details about a preferred location\n" +
        "Task: Provide coordinates, city, country, and a friendly description\n" +
        "Note: you can make similar guesses if specific information is not provided\n" +
        "Friendly Description: Keep the description warm and inviting and short (max 15 words)\n" +
        "If you can't find anything you can return random location" +
        "Size of Description: Concise and brief\n" +
        "Output: JSON object only,\n" +
        "make JSON format accordingly:" +
        "data class Content(\n" +
        "    val coordinates: Coordinates,\n" +
        "    val city: String,\n" +
        "    val country: String,\n" +
        "    val description: String\n" +
        ")" +
        "data class Coordinates(\n" +
        "    val latitude: Double,\n" +
        "    val longitude: Double\n" +
        ")" +
        "in the RESPONSE GIVE ONLY JSON object NOTHING ELSE"