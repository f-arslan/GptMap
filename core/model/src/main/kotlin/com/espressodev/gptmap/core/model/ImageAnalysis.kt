package com.espressodev.gptmap.core.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


open class RealmImageAnalysis: RealmObject {
    @PrimaryKey
    val _id: ObjectId = ObjectId()
    var userId: String = ""
    var imageUrl: String = ""
    var title: String = ""
    var messages: RealmList<RealmImageMessage>? = null
}

open class RealmImageMessage: EmbeddedRealmObject {

}

