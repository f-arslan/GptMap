package com.espressodev.gptmap.core.model.realm

import io.realm.kotlin.types.EmbeddedRealmObject

open class RealmContent: EmbeddedRealmObject {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var city: String = ""
    var district: String = ""
    var country: String = ""
    var poeticDescription: String = ""
    var normalDescription: String = ""
}