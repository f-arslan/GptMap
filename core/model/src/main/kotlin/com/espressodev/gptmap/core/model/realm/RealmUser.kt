package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.Provider
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class RealmUser : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var firebaseId: String = ""
    var email: String = ""
    var profilePictureUrl: String = ""
    var fcmToken: String = ""
    var isEmailVerified: Boolean = false
    var provider: String = Provider.DEFAULT.name
}