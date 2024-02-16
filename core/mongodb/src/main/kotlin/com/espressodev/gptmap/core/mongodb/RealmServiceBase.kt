package com.espressodev.gptmap.core.mongodb

import android.util.Log
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realm
import com.espressodev.gptmap.core.mongodb.module.RealmModule.realmUser
import io.realm.kotlin.MutableRealm

abstract class RealmServiceBase {
    protected val realmUserId: String
        get() = realmUser.id

    protected suspend fun <T> performRealmTransaction(block: MutableRealm.() -> T): Result<T> =
        runCatching {
            realm.write {
                block()
            }
        }.onFailure {
            Log.e("RealmServiceBase", "Operation failed: $it")
            Result.failure<Throwable>(it)
        }
}
