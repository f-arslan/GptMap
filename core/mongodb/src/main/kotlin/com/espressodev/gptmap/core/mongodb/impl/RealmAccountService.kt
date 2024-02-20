package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.Exceptions.RealmUserNotLoggedInException
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.mongodb.RealmAccountRepository
import com.espressodev.gptmap.core.mongodb.module.RealmManager
import com.espressodev.gptmap.core.mongodb.module.RealmManager.app
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealmAccountService @Inject constructor(@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher) :
    RealmAccountRepository {
    override suspend fun loginWithEmail(token: String): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            val user = app.login(Credentials.jwt(token))
                .also { if (!it.loggedIn) throw RealmUserNotLoggedInException() }
            RealmManager.initRealm(user)
        }
    }

    override suspend fun logOut() {
        withContext(ioDispatcher) {
            app.currentUser?.logOut()
        }
    }

    override suspend fun revokeAccess(): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            app.currentUser?.delete()
        }
    }
}
