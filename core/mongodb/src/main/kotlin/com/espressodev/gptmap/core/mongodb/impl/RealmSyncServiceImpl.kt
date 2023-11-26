package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.model.realm.Dog
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.impl.RealmApp.app
import com.espressodev.gptmap.core.mongodb.impl.RealmConfig.APP_ID
import com.espressodev.gptmap.core.mongodb.impl.RealmConfig.BASE_URL
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object RealmApp {
    val app = App.create(
        AppConfiguration.Builder(APP_ID)
            .baseUrl(BASE_URL)
            .build())
}

class RealmSyncServiceImpl : RealmSyncService {

    private val realm: Realm
    private val config: SyncConfiguration
    private val currentUser: User
        get() = app.currentUser!!


    init {
        config = SyncConfiguration.Builder(currentUser, setOf(RealmUser::class))
            .errorHandler { _: SyncSession, error: SyncException ->
                Log.e("RealmSyncServiceImpl", "errorHandler: ", error)
            }
            .log(LogLevel.ALL)
            .build()
        realm = Realm.open(config)
        Log.d("RealmSyncServiceImpl", "init: ${realm.configuration.path}")
    }

    override suspend fun addUser(user: RealmUser): Unit = withContext(Dispatchers.Default) {
        val dog = Dog()
        realm.writeBlocking {
            copyToRealm(dog)
        }
    }

    override fun pauseSync() {
        TODO("Not yet implemented")
    }

    override fun resumeSync() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}




object RealmConfig {
    const val APP_ID = "application-0-zvkev"
    const val APP_URL = "https://realm.mongodb.com/groups/655bb2cf6056711543484296/apps/6560f20e92bda878b6469402"
    const val BASE_URL = "https://realm.mongodb.com"
    const val CLIENT_API_BASE_URL = "https://europe-west1.gcp.realm.mongodb.com"
    const val DATA_API_BASE_URL = "https://europe-west1.gcp.data.mongodb-api.com"
    const val DATA_EXPLORER_LINK = "https://cloud.mongodb.com/links/655bb2cf6056711543484296/explorer/gptmapCluster/database/collection/find"
    const val DATA_SOURCE_NAME = "mongodb-atlas"
}
