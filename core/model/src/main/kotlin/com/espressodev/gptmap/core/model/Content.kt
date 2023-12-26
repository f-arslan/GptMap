package com.espressodev.gptmap.core.model


data class Content(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val district: String = "",
    val country: String = "",
    val poeticDescription: String = "",
    val normalDescription: String = ""
)