package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import com.espressodev.gptmap.core.mongodb.module.RealmModule.user
import io.realm.kotlin.UpdatePolicy


class RealmSyncServiceImpl : RealmSyncService {

    override suspend fun addUser(realmUser: RealmUser): Result<Boolean> = runCatching {
        realm.write {
            copyToRealm(realmUser.apply {
                userId = user.id
            }, updatePolicy = UpdatePolicy.ALL)
        }
        true
    }.onFailure {
        Log.e("RealmSyncServiceImpl", "addUser: failure $it")
        Result.failure<Throwable>(it)
    }
}



