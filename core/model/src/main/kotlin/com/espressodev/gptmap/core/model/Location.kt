package com.espressodev.gptmap.core.model

import androidx.compose.runtime.Stable
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import io.realm.kotlin.ext.toRealmList

@Stable
data class Location(
    val id: String = "default",
    val content: Content = Content(),
    val locationImages: List<LocationImage> = List(2) { LocationImage("", "") },
    val addToFavouriteButtonState: Boolean = true
) {
    fun toRealmLocation(): RealmFavourite = RealmFavourite().apply {
        favouriteId = this@Location.id
        content = this@Location.content.toRealmContent()
        locationImages =
            this@Location.locationImages.map { it.toRealmLocationImage() }.toRealmList()
    }
}
