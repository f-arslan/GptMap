package com.espressodev.gptmap.api.palm.impl

import com.espressodev.gptmap.api.palm.PalmApi
import com.espressodev.gptmap.api.palm.PalmService
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.PromptUtil.locationPreText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PalmServiceImpl(private val palmApi: PalmApi) : PalmService {
    override suspend fun getLocationInfo(textContent: String): Result<Location> =
        withContext(Dispatchers.IO) {
            runCatching {
                palmApi.generateText(locationPreText + textContent).toLocation()
            }
        }
}
