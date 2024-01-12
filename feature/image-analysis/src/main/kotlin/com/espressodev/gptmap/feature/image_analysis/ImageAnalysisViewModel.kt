package com.espressodev.gptmap.feature.image_analysis

import android.util.Log
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.Exceptions.RealmFailedToLoadImageAnalysisException
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ImageAnalysisViewModel @Inject constructor(
    private val realmSyncService: RealmSyncService,
    logService: LogService
) : GmViewModel(logService) {
    private val _imageAnalysis: MutableStateFlow<Response<ImageAnalysis>> =
        MutableStateFlow(Response.Loading)
    val imageAnalysis = _imageAnalysis.asStateFlow()

    fun initializeImageAnalysis(imageId: String) = launchCatching {
        realmSyncService.getImageAnalysis(imageId)
            .onSuccess {
                _imageAnalysis.value = Response.Success(it)
            }.onFailure {
                _imageAnalysis.value = Response.Failure(RealmFailedToLoadImageAnalysisException())
            }
    }
}
