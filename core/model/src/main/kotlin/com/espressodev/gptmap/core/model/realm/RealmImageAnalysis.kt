package com.espressodev.gptmap.core.model.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class RealmImageAnalysis: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var imageId: String = ""
    var userId: String = ""
    var imageUrl: String = ""
    var title: String = ""
    var messages: RealmList<RealmImageMessage>? = realmListOf()
}

open class RealmImageMessage: EmbeddedRealmObject {
    var request: String = ""
    var response: String = ""
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)
}