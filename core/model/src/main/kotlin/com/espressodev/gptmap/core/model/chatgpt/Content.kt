package com.espressodev.gptmap.core.model.chatgpt

import com.espressodev.gptmap.core.model.realm.RealmContent
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val coordinates: Coordinates = Coordinates(),
    val city: String = "Istanbul",
    val district: String? = null,
    val country: String = "Turkey",
    val poeticDescription: String = "Place of the most beautiful sunsets",
    val normalDescription: String = ""
) {
    fun toDistrictAndCountry() = district?.let {
        "$district, $country"
    } ?: country

    fun toRealmContent(): RealmContent = RealmContent().apply {
        latitude = coordinates.latitude
        longitude = coordinates.longitude
        city = this@Content.city
        district = this@Content.country
        country = this@Content.country
        poeticDescription = this@Content.poeticDescription
        normalDescription = this@Content.normalDescription
    }

    fun toPoeticDescWithDecor() = "\"$poeticDescription\""
}
