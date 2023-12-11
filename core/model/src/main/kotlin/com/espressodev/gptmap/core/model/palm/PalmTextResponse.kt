package com.espressodev.gptmap.core.model.palm

import kotlinx.serialization.Serializable

@Serializable
data class PalmTextResponse(
    val candidates: List<PalmOutputText>
)