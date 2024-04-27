package com.espressodev.gptmap.core.mongodb

import android.util.Log
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realm
import com.espressodev.gptmap.core.mongodb.module.RealmManager.realmUser
import io.realm.kotlin.MutableRealm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface RealmDataSourceBase {
    val realmUserId: String
        get() = realmUser.id

    suspend fun <T> performRealmTransaction(
        dispatcher: CoroutineDispatcher,
        block: MutableRealm.() -> T
    ): Result<T> =
        runCatching {
            withContext(dispatcher) {
                realm.write(block)
            }
        }.onFailure {
            Log.e("RealmDataSourceBase", "Operation failed: $it")
            Result.failure<Throwable>(it)
        }
}
