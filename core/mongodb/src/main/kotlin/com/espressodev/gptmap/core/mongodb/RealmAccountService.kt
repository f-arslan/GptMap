package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Response

interface RealmAccountService {
    suspend fun loginWithUsername(email: String, password: String): Response<Boolean>
    suspend fun loginWithGmail(token: String): Response<Boolean>
}