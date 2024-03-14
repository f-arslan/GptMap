package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.ImageMessage
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.UUID

open class RealmImageAnalysis : RealmObject {
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()
    var imageId: String = ""
    var userId: String = ""
    var imageUrl: String = ""
    var title: String = ""
    var imageType: String = ""
    var messages: RealmList<RealmImageMessage>? = realmListOf()
    var date = RealmInstant.from(System.currentTimeMillis(), 0)
}

fun RealmImageAnalysis.toImageAnalysis() = ImageAnalysis(
    id = id.toHexString(),
    imageId = imageId,
    userId = userId,
    imageUrl = imageUrl,
    title = title,
    imageType = imageType,
    messages = messages?.map { it.toImageMessage() } ?: emptyList(),
    date = date.toJavaInstant().toLocalDateTime()
)

open class RealmImageMessage : EmbeddedRealmObject {
    var id: String = UUID.randomUUID().toString()
    var request: String = ""
    var response: String = ""
    var token: Int = 0
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)
}

fun RealmImageMessage.toImageMessage() = ImageMessage(
    id = id,
    request = request,
    response = response,
    token = token,
    date = date.toJavaInstant().toLocalDateTime()
)
