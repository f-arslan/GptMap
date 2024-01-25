package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.model.realm.toFavourite
import com.espressodev.gptmap.core.model.realm.toImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realmUser
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealmSyncServiceImpl : RealmSyncService {
    private val realmUserId: String
        get() = realmUser.id

    override suspend fun saveUser(realmUser: RealmUser): Result<Boolean> = runCatching {
        realm.write {
            copyToRealm(
                realmUser.apply {
                    userId = realmUserId
                }, updatePolicy = UpdatePolicy.ALL
            )
        }
        true
    }.onFailure {
        Log.e("RealmSyncServiceImpl", "addUser: failure $it")
        Result.failure<Throwable>(it)
    }

    override suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Boolean> =
        runCatching {
            realm.write {
                copyToRealm(
                    realmFavourite.apply {
                        userId = realmUserId
                    }, updatePolicy = UpdatePolicy.ALL
                )
            }
            true
        }.onFailure {
            Log.e("RealmSyncServiceImpl", "addLocation: failure $it")
            Result.failure<Throwable>(it)
        }

    override suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Boolean> {
        return runCatching {
            realm.write {
                copyToRealm(
                    realmImageAnalysis.apply {
                        userId = realmUserId
                    }, updatePolicy = UpdatePolicy.ALL
                )
            }
            true
        }.onFailure {
            Log.e("RealmSyncServiceImpl", "addImageAnalysis: failure $it")
            Result.failure<Throwable>(it)
        }
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

    override fun getImageAnalyses(): Flow<List<ImageAnalysis>> =
        realm.query<RealmImageAnalysis>("userId == $0", realmUserId).find().asFlow().map {
            it.list.map { realmImageAnalysis -> realmImageAnalysis.toImageAnalysis() }
        }

    override fun isUserInDatabase(): Result<Boolean> = runCatching {
        realm.query<RealmUser>("userId == $0", realmUserId).first().find() != null
    }

    override suspend fun deleteImageAnalysis(imageAnalysisId: String): Result<Boolean> = runCatching {
        realm.write {
            val imageAnalysisToDelete: RealmImageAnalysis = query<RealmImageAnalysis>("userId == $0 AND imageId == $1", realmUserId, imageAnalysisId)
                .find()
                .first()
            delete(imageAnalysisToDelete)
        }
        true
    }

    override suspend fun deleteImageAnalyses(imageAnalysesIds: Set<String>): Result<Unit> = runCatching {
        realm.write {
            val imageAnalysesToDelete: RealmResults<RealmImageAnalysis> = query<RealmImageAnalysis>("userId == $0 AND imageId IN $1", realmUserId, imageAnalysesIds)
                .find()
            delete(imageAnalysesToDelete)
        }
    }

    override suspend fun updateImageAnalysisText(
        imageAnalysisId: String,
        text: String
    ): Result<Boolean> = runCatching {
        realm.write {
            val imageAnalysisToUpdate: RealmImageAnalysis = query<RealmImageAnalysis>("userId == $0 AND imageId == $1", realmUserId, imageAnalysisId)
                .find()
                .first()
            findLatest(imageAnalysisToUpdate)?.let { realmImageAnalysis ->
               realmImageAnalysis.title = text
            }
        }
        true
    }

    override suspend fun deleteFavourite(favouriteId: String): Result<Boolean> = runCatching {
        realm.write {
            val favouriteToDelete: RealmFavourite = query<RealmFavourite>("userId == $0 AND favouriteId == $1", realmUserId, favouriteId)
                .find()
                .first()
            delete(favouriteToDelete)
        }
        true
    }

    override suspend fun updateFavouriteText(favouriteId: String, text: String): Result<Boolean> = runCatching {
        realm.write {
            val favouriteToUpdate: RealmFavourite = query<RealmFavourite>("userId == $0 AND favouriteId == $1", realmUserId, favouriteId)
                .find()
                .first()
            findLatest(favouriteToUpdate)?.let { realmFavourite ->
               realmFavourite.title = text
            }
        }
        true
    }
}
