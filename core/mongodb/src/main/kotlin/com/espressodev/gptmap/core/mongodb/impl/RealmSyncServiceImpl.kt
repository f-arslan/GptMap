package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import com.espressodev.gptmap.core.mongodb.module.RealmModule.user
import io.realm.kotlin.UpdatePolicy


class RealmSyncServiceImpl : RealmSyncService {

    override suspend fun addUser(realmUser: RealmUser) {
        runCatching {
            realm.write {
                copyToRealm(realmUser.apply {
                    userId = user!!.id
                }, updatePolicy = UpdatePolicy.ALL)
            }
        }.onSuccess {
            Log.d("RealmSyncServiceImpl", "addUser: success")
        }.onFailure {
            Log.d("RealmSyncServiceImpl", "addUser: failure $it")
        }

    }

    override fun close() {
       realm.close()
    }
}



