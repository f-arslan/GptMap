package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmAccountServiceImpl @Inject constructor(private val app: App) : RealmAccountService {
    override suspend fun loginWithUsername(email: String, password: String): Response<Boolean> {
        TODO()
    }

    override suspend fun loginWithGmail(token: String) = try {
        app.login(Credentials.jwt(token))
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }
}
