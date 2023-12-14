package com.espressodev.gptmap.core.model.chatgpt

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val latitude: Double = 41.0082,
    val longitude: Double = 28.9784
) {
    fun toLatLng() = LatLng(latitude, longitude)
}