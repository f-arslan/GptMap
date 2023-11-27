package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.module.RealmModule
import com.espressodev.gptmap.core.mongodb.module.RealmModule.app
import io.realm.kotlin.mongodb.Credentials
import javax.inject.Singleton

@Singleton
class RealmAccountServiceImpl : RealmAccountService {
    override suspend fun loginWithEmail(token: String) {
        try {
            app.login(Credentials.jwt(token))
            RealmModule.initRealm(app.currentUser!!)
        } catch (e: Exception) {
            Log.e(TAG, "loginWithEmail: ", e)
        }
    }

    override suspend fun logOut() {
        app.currentUser?.logOut()
    }


    private companion object {
        const val TAG = "RealmAccountServiceImpl"
    }
}
