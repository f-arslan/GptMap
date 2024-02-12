package com.espressodev.gptmap.core.model.unsplash

import com.espressodev.gptmap.core.model.realm.RealmLocationImage

data class LocationImage(
    val id: String = "",
    val analysisId: String = "",
    val imageUrl: String,
    val imageAuthor: String,
) {
    fun toRealmLocationImage() = RealmLocationImage().apply {
        id = this@LocationImage.id
        analysisId = this@LocationImage.analysisId
        imageUrl = this@LocationImage.imageUrl
        imageAuthor = this@LocationImage.imageAuthor
    }
}
