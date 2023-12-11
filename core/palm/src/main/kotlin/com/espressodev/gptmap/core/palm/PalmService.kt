package com.espressodev.gptmap.core.palm


interface PalmService {
    suspend fun getLocationInfo(textContent: String): Result<String>
}