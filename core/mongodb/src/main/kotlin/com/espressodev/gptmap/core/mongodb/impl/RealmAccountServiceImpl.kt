package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.MongoService.APP_ID
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealmAccountServiceImpl : RealmAccountService {
    override suspend fun login(token: String): Response<Boolean> = try {
        withContext(Dispatchers.IO) {
            App.create(APP_ID).login(Credentials.jwt(token)).loggedIn.also { println(it) }
            Response.Success(true)
        }
    } catch (e: Exception) {
        Response.Failure(e)
    }
}