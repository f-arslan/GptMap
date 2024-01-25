package com.espressodev.gptmap.core.model.chatgpt

import com.espressodev.gptmap.core.model.Location
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChatgptResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    @SerializedName("object")
    val `object`: String,
    val usage: Usage
) {
    fun toLocation(responseContent: String, id: String): Location {
        val content = Json.decodeFromString<Content>(responseContent)
        return Location(id = id, content = content)
    }
}
