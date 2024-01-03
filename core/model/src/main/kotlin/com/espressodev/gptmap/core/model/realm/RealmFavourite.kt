package com.espressodev.gptmap.core.model.realm

import com.espressodev.gptmap.core.model.Content
import com.espressodev.gptmap.core.model.Favourite
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

open class RealmFavourite : RealmObject {
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()
    var userId: String = ""
    var favouriteId: String = ""
    var title: String = ""
    var placeholderImageUrl: String = ""
    var locationImages: RealmList<RealmLocationImage>? = null
    var content: RealmContent? = null
    var date: RealmInstant = RealmInstant.from(System.currentTimeMillis(), 0)
}

fun RealmInstant.toJavaInstant(): Instant {
    return Instant.ofEpochSecond(this.epochSeconds, this.nanosecondsOfSecond.toLong())
}

fun Instant.toLocalDateTime(): LocalDateTime {
    return this.atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun RealmFavourite.toFavourite(): Favourite = Favourite(
    id = this.id.toHexString(),
    userId = this.userId,
    favouriteId = this.favouriteId,
    title = this.title,
    placeholderImageUrl = this.placeholderImageUrl,
    locationImages = this.locationImages?.map { it.toLocationImage() } ?: emptyList(),
    content = this.content?.toContent() ?: Content(),
    date = this.date.toJavaInstant().toLocalDateTime()
)
