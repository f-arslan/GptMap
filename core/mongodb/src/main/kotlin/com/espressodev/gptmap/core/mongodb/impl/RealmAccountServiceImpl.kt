package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.RealmApp.app
import io.realm.kotlin.mongodb.Credentials
import javax.inject.Singleton

@Singleton
class RealmAccountServiceImpl : RealmAccountService {
    override suspend fun loginWithEmail(token: String) = try {
        app.login(Credentials.jwt(token))
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }
}
