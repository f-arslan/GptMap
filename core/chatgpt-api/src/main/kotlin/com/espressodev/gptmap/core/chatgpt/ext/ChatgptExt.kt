package com.espressodev.gptmap.core.chatgpt.ext

import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest


internal fun ChatgptRequest.mergeMessageWithPreText(): ChatgptRequest {
    val currentContent = messages.first().content
    val requestContent = "${PRE_TEXT}$currentContent"
    val newMessages = listOf(messages.first().copy(content = requestContent))
    return copy(messages = newMessages)
}


private const val PRE_TEXT = "User Input: Desired place, and a close guess if not exact.\n" +
        "Provide coordinates, city, district, country, poetic description (15 words or less), and a normal description (under 50 words, covering area, population, key landmarks).\n" +
        "Return a close location if the exact one isn't found." +
        "Output: JSON object only,\n" +
        "make JSON format accordingly:" +
        "data class Content(\n" +
        "    val coordinates: Coordinates,\n" +
        "    val city: String,\n" +
        "    val district: String? = null,\n" +
        "    val country: String,\n" +
        "    val poeticDescription: String,\n" +
        "    val normalDescription: String\n" +
        ")" +
        "data class Coordinates(\n" +
        "    val latitude: Double,\n" +
        "    val longitude: Double\n" +
        ")" +
        "in the RESPONSE GIVE ONLY JSON object NOTHING ELSE"