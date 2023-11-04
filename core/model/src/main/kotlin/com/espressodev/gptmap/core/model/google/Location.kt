package com.espressodev.gptmap.core.model.google

import androidx.compose.runtime.Stable


@Stable
data class Location(
    val countryName: String,
    val postalCode: String,
    val buildingNo: String?,
    val city: String,
    val district: String,
    val neighbourhoodStreet: String,
)