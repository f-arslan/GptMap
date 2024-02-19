package com.espressodev.gptmap.core.mongodb

import android.util.Log
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realmUser
import io.realm.kotlin.MutableRealm

abstract class RealmDataSourceBase {
    protected val realmUserId: String
        get() = realmUser.id

    protected suspend fun <T> performRealmTransaction(block: MutableRealm.() -> T): Result<T> =
        runCatching {
            realm.write {
                block()
            }
        }.onFailure {
            Log.e("RealmDataSourceBase", "Operation failed: $it")
            Result.failure<Throwable>(it)
        }
}
