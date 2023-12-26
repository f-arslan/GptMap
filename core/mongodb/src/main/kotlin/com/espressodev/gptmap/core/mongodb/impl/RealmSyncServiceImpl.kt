package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realmUser
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class RealmSyncServiceImpl : RealmSyncService {

    override suspend fun saveUser(realmUser: RealmUser): Result<Boolean> = runCatching {
        realm.write {
            copyToRealm(
                realmUser.apply {
                    userId = RealmModule.realmUser.id
                }, updatePolicy = UpdatePolicy.ALL
            )
        }
        true
    }.onFailure {
        Log.e("RealmSyncServiceImpl", "addUser: failure $it")
        Result.failure<Throwable>(it)
    }

    override suspend fun saveLocation(realmLocation: RealmFavourite): Result<Boolean> = runCatching {
        realm.write {
            copyToRealm(
                realmLocation.apply {
                    userId = realmUser.id
                }, updatePolicy = UpdatePolicy.ALL
            )
        }
        true
    }.onFailure {
        Log.e("RealmSyncServiceImpl", "addLocation: failure $it")
        Result.failure<Throwable>(it)
    }



    override fun isUserInDatabase(): Boolean =
        realm.query<RealmUser>("userId == $0", realmUser.id).first().find() != null

}



