package com.espressodev.gptmap.core.model.chatgpt

import com.espressodev.gptmap.core.model.Location
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.json.Json

data class ChatgptResponse(
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("created")
    val created: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("object")
    val `object`: String,
    @SerializedName("usage")
    val usage: Usage
) {
    fun toLocation(responseContent: String, id: String): Location {
        val content = Json.decodeFromString<Content>(responseContent)
        return Location(id = id, content = content)
    }
}