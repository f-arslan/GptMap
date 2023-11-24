package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.model.realm.RealmUser
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class RealmDatabase {
    val realm: Realm by lazy {
        val configuration = RealmConfiguration.create(schema = setOf(RealmUser::class))
        Realm.open(configuration)
    }

    suspend fun addUser(realmUser: RealmUser) {
        realm.writeBlocking {
            copyToRealm(realmUser)
        }
    }
}