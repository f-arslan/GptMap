package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.toFavourite
import com.espressodev.gptmap.core.mongodb.FavouriteRealmRepository
import com.espressodev.gptmap.core.mongodb.RealmDataSourceBase
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavouriteRealmDataSource @Inject constructor(@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher) :
    FavouriteRealmRepository, RealmDataSourceBase {

    override suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Unit> =
        performRealmTransaction(ioDispatcher) {
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

    override suspend fun getFavourite(id: String): Favourite = withContext(ioDispatcher) {
        realm.query<RealmFavourite>("userId == $0 AND favouriteId == $1", realmUserId, id)
            .find()
            .first()
            .toFavourite()
    }

    override suspend fun deleteFavourite(favouriteId: String): Result<Unit> =
        performRealmTransaction(ioDispatcher) {
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
        performRealmTransaction(ioDispatcher) {
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
    ): Result<Unit> = performRealmTransaction(ioDispatcher) {
        val favouriteToUpdate: RealmFavourite = query<RealmFavourite>(
            "userId == $0 AND favouriteId == $1",
            realmUserId,
            favouriteId
        )
            .find()
            .first()
        findLatest(favouriteToUpdate)?.let { realmFavourite ->
            realmFavourite.locationImages?.find { it.id == messageId }?.analysisId =
                imageAnalysisId
        }
    }

    override suspend fun resetImageAnalysisId(imageAnalysisId: String): Result<Unit> =
        performRealmTransaction(ioDispatcher) {
            val favouriteToUpdate: RealmFavourite = query<RealmFavourite>(
                "userId == $0 AND locationImages.analysisId == $1",
                realmUserId,
                imageAnalysisId
            )
                .find()
                .first()
            findLatest(favouriteToUpdate)?.let { realmFavourite ->
                realmFavourite.locationImages?.find { it.analysisId == imageAnalysisId }?.analysisId =
                    ""
            }
        }
}
