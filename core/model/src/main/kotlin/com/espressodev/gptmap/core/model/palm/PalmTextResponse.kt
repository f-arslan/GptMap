package com.espressodev.gptmap.core.model.palm

import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.chatgpt.Content
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class PalmTextResponse(
    val candidates: List<PalmOutputText>
) {
    fun toLocation(): Location {
        val content = Json.decodeFromString<Content>(candidates.last().output)
        return Location(id = UUID.randomUUID().toString(), content = content)
    }
}
