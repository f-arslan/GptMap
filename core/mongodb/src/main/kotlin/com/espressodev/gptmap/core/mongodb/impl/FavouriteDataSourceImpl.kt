package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.toFavourite
import com.espressodev.gptmap.core.mongodb.FavouriteDataSource
import com.espressodev.gptmap.core.mongodb.RealmDataSourceBase
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteDataSourceImpl : FavouriteDataSource, RealmDataSourceBase() {

    override suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Unit> =
        performRealmTransaction {
            copyToRealm(
                realmFavourite.apply {
                    userId = realmUserId
                },
                updatePolicy = UpdatePolicy.ALL
            )
        }

    override fun getFavourites(): Flow<List<Favourite>> =
        realm.query<RealmFavourite>("userId == $0", realmUserId).find().asFlow().map {
            it.list.map { realmFavourite -> realmFavourite.toFavourite() }
        }

    override fun getFavourite(id: String): Favourite =
        realm.query<RealmFavourite>("userId == $0 AND favouriteId == $1", realmUserId, id)
            .find()
            .first()
            .toFavourite()

    override suspend fun deleteFavourite(favouriteId: String): Result<Unit> =
        performRealmTransaction {
            val favouriteToDelete: RealmFavourite = query<RealmFavourite>(
                "userId == $0 AND favouriteId == $1",
                realmUserId,
                favouriteId
            )
                .find()
                .first()
            delete(favouriteToDelete)
        }

    override suspend fun updateFavouriteText(favouriteId: String, text: String): Result<Unit> =
        performRealmTransaction {
            val favouriteToUpdate: RealmFavourite = query<RealmFavourite>(
                "userId == $0 AND favouriteId == $1",
                realmUserId,
                favouriteId
            )
                .find()
                .first()
            findLatest(favouriteToUpdate)?.let { realmFavourite ->
                realmFavourite.title = text
            }
        }

    override suspend fun updateImageAnalysisId(
        favouriteId: String,
        messageId: String,
        imageAnalysisId: String
    ): Result<Unit> = performRealmTransaction {
        val favouriteToUpdate: RealmFavourite = query<RealmFavourite>(
            "userId == $0 AND favouriteId == $1",
            realmUserId,
            favouriteId
        )
            .find()
            .first().also(::println)
        findLatest(favouriteToUpdate)?.let { realmFavourite ->
            realmFavourite.locationImages?.find { it.id == messageId }?.analysisId =
                imageAnalysisId
        }
    }
}
