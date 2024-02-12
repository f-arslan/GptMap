package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.unsplash.LocationImage
import io.realm.kotlin.types.EmbeddedRealmObject

open class RealmLocationImage : EmbeddedRealmObject {
    var id: String = ""
    var analysisId: String = ""
    var imageUrl: String = ""
    var imageAuthor: String = ""
}

fun RealmLocationImage.toLocationImage(): LocationImage = LocationImage(
    id = this.id,
    analysisId = this.analysisId,
    imageUrl = this.imageUrl,
    imageAuthor = this.imageAuthor
)
