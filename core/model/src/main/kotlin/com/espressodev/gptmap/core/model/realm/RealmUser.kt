package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.Provider
import io.realm.kotlin.internal.interop.RealmObjectT
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class RealmUser : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var userId: String = ""
    var firebaseId: String = ""
    var email: String = ""
    var profilePictureUrl: String = ""
    var fcmToken: String = ""
    var provider: String = Provider.DEFAULT.name
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)

    override fun toString(): String =
        "RealmUser(_id=$_id, userId='${userId}' firebaseId='$firebaseId', email='$email', profilePictureUrl='$profilePictureUrl', fcmToken='$fcmToken', provider='$provider', date=$date)"

}

class Hero(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var power: String = ""
    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}