package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.realm.Hero
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


class RealmSyncServiceImpl @Inject constructor(private val app: App) : RealmSyncService {

    private val realm: Realm
    private val config: SyncConfiguration
    private val currentUser: User
        get() = app.currentUser!!

    init {
        config = SyncConfiguration.Builder(currentUser, setOf(Hero::class, RealmUser::class))
            .waitForInitialRemoteData(60.seconds)
            .initialSubscriptions { realm: Realm ->
                add(realm.query<Hero>("owner_id == $0", currentUser.id))
                add(realm.query<RealmUser>("userId == $0", currentUser.id))
            }
            .errorHandler { _: SyncSession, error: SyncException ->
                Log.e("RealmSyncServiceImpl", "errorHandler: ", error)
            }
            .log(LogLevel.ALL)
            .waitForInitialRemoteData()
            .build()
        realm = Realm.open(config)
        Log.d(
            "RealmSyncServiceImpl",
            "Successfully opened synced realm: ${realm.configuration.name}"
        )

        CoroutineScope(Dispatchers.Main).launch {
            realm.subscriptions.waitForSynchronization()
        }
    }

    override suspend fun addUser(realmUser: RealmUser) {
        runCatching {
            realm.write {
                copyToRealm(realmUser.apply {
                    userId = currentUser.id
                }, updatePolicy = UpdatePolicy.ALL)
            }
        }.onSuccess {
            Log.d("RealmSyncServiceImpl", "addUser: success")
        }.onFailure {
            Log.d("RealmSyncServiceImpl", "addUser: failure $it")
        }

    }

    override fun close() {
        TODO("Not yet implemented")
    }
}



