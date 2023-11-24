package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.Provider
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class RealmUser : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var userId: String = ""
    var firebaseId: String = ""
    var email: String = ""
    var profilePictureUrl: String = ""
    var fcmToken: String = ""
    var isEmailVerified: Boolean = false
    var provider: String = Provider.DEFAULT.name
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)

    override fun toString(): String =
        "RealmUser(_id=$_id, userId='$userId', firebaseId='$firebaseId', email='$email', profilePictureUrl='$profilePictureUrl', fcmToken='$fcmToken', isEmailVerified=$isEmailVerified, provider='$provider', date=$date)"

}