package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.model.realm.toFavourite
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realmUser
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
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

    override fun isUserInDatabase(): Boolean =
        realm.query<RealmUser>("userId == $0", realmUserId).first().find() != null

}



