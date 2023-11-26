package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Response

interface RealmAccountService {
    suspend fun loginWithEmail(token: String): Response<Boolean>
}