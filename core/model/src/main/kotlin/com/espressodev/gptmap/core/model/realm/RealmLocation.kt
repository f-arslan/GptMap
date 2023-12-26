package com.espressodev.gptmap.core.model.realm

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class RealmLocation: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var userId: String = ""
    var locationId: String = ""
    var locationTitle: String = ""
    var placeholderImageUrl: String = ""
    var locationImages: RealmList<RealmLocationImage>? = null
    var content: RealmContent? = null
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)
}



