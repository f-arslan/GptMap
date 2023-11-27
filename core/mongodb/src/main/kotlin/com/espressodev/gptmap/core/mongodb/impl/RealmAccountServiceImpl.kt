package com.espressodev.gptmap.core.mongodb.impl

import android.util.Log
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmAccountServiceImpl @Inject constructor(private val app: App) : RealmAccountService {
    override suspend fun loginWithEmail(token: String) {
        try {
            app.login(Credentials.jwt(token))
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
