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
    val locationImages: List<LocationImage> = List(2) {
        LocationImage(
            id = "",
            analysisId = "",
            imageUrl = "",
            imageAuthor = "",
        )
    },
    val addToFavouriteButtonState: Boolean = true,
    val favouriteId: String = ""
) {
    fun toRealmFavourite(): RealmFavourite = RealmFavourite().apply {
        favouriteId = this@Location.id
        content = this@Location.content.toRealmContent()
        locationImages =
            this@Location.locationImages.map { it.toRealmLocationImage() }.toRealmList()
    }
}
