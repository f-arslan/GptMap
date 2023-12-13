package com.espressodev.gptmap.core.model.ext

import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.chatgpt.Content
import kotlinx.serialization.json.Json
import java.util.UUID


fun String.toLocation(): Location =
    Location(id = UUID.randomUUID().toString(), Json.decodeFromString<Content>(this))

