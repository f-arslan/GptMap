package com.espressodev.gptmap.core.palm.impl

import android.util.Log
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.palm.PalmService
import com.espressodev.gptmap.core.palm.module.PalmApi

class PalmServiceImpl(private val palmApi: PalmApi) : PalmService {
    override suspend fun getLocationInfo(textContent: String): Result<String> {
        val palmText = palmApi.generateText(textContent)
        val response = palmText.candidates.last().output
        Log.d(classTag(), response)
        return Result.success(response)
    }
}