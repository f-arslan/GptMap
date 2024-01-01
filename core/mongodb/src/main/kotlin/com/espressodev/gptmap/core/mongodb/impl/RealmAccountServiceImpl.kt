package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.module.RealmModule
import com.espressodev.gptmap.core.mongodb.module.RealmModule.app
import io.realm.kotlin.mongodb.Credentials

class RealmAccountServiceImpl : RealmAccountService {
    override suspend fun loginWithEmail(token: String): Result<Boolean> = runCatching {
        val user = app.login(Credentials.jwt(token))
        RealmModule.initRealm(user)
        true
    }.onFailure {
        Result.failure<Throwable>(it)
    }

    override suspend fun logOut() {
        app.currentUser?.logOut()
    }
}
