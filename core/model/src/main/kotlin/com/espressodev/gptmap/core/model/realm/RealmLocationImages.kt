package com.espressodev.gptmap.core.model.realm

import io.realm.kotlin.types.EmbeddedRealmObject

open class RealmLocationImage: EmbeddedRealmObject {
    var imageUrl: String = ""
    var imageAuthor: String = ""
}