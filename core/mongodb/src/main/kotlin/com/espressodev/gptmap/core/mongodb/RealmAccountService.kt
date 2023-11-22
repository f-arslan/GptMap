package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Response

interface RealmAccountService {
    suspend fun login(token: String): Response<Boolean>
}