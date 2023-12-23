package com.espressodev.gptmap.core.model.unsplash

import com.espressodev.gptmap.core.model.realm.RealmLocationImage

data class LocationImage(
    val imageUrl: String,
    val imageAuthor: String,
) {
    fun toRealmLocationImage() = RealmLocationImage().apply {
        imageUrl = this@LocationImage.imageUrl
        imageAuthor = this@LocationImage.imageAuthor
    }
}
