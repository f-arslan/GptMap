package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.firebase.Provider
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class RealmUser : RealmObject {
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()
    var userId: String = ""
    var firebaseId: String = ""
    var email: String = ""
    var profilePictureUrl: String = ""
    var fcmToken: String = ""
    var provider: String = Provider.DEFAULT.name
    private var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)

    override fun toString(): String =
        "RealmUser(_id=$id, userId='$userId' firebaseId='$firebaseId', email='$email', " +
            "profilePictureUrl='$profilePictureUrl', fcmToken='$fcmToken', " +
            "provider='$provider', date=$date)"
}
