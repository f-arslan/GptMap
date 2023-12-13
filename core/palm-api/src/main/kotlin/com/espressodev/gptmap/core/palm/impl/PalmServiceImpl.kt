package com.espressodev.gptmap.core.palm.impl

import android.util.Log
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.model.utils.PromptUtil.locationPreText
import com.espressodev.gptmap.core.palm.PalmService
import com.espressodev.gptmap.core.palm.PalmApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PalmServiceImpl(private val palmApi: PalmApi) : PalmService {
    override suspend fun getLocationInfo(textContent: String): Result<Location> =
        withContext(Dispatchers.IO) {
            try {
                palmApi.generateText(locationPreText + textContent).toLocation().let {
                    Log.d(classTag(), it.content.toString())
                    Result.success(it)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}