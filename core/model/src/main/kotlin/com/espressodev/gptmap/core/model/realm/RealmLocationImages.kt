package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.unsplash.LocationImage
import io.realm.kotlin.types.EmbeddedRealmObject

open class RealmLocationImage : EmbeddedRealmObject {
    var imageUrl: String = ""
    var imageAuthor: String = ""
}

fun RealmLocationImage.toLocationImage(): LocationImage = LocationImage(
    imageUrl = this.imageUrl,
    imageAuthor = this.imageAuthor
)