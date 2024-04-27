package com.espressodev.gptmap.core.model

import androidx.compose.runtime.Stable
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import io.realm.kotlin.ext.toRealmList

@Stable
data class Location(
    val id: String,
    val content: Content,
    val locationImages: List<LocationImage>,
    val isAddedToFavourite: Boolean,
    val favouriteId: String
)

fun Location.toRealmFavourite(): RealmFavourite = RealmFavourite().apply {
    favouriteId = this@Location.id
    content = this@Location.content.toRealmContent()
    locationImages =
        this@Location.locationImages.map { it.toRealmLocationImage() }.toRealmList()
}

val locationDefault: Location =
    Location(
        id = "", content = Content(),
        locationImages = List(2) { LocationImage(imageUrl = "", imageAuthor = "") },
        isAddedToFavourite = true,
        favouriteId = ""
    )
